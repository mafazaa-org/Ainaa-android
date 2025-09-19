package com.mafazaa.ainaa.data

import com.google.gson.Gson
import com.mafazaa.ainaa.model.ScreenAnalysis
import com.mafazaa.ainaa.model.ScriptCode
import com.mafazaa.ainaa.model.ScriptResult
import com.mafazaa.ainaa.model.repo.ScriptRepo
import org.mozilla.javascript.Context
import org.mozilla.javascript.ContextFactory
import org.mozilla.javascript.Scriptable

class JsEngine(
    private val useTimeout: Boolean = true,
    private val instructionLimit: Int = 50_000 // only used when useTimeout==true
) : ScriptRepo {

    private val codes = mutableListOf<ScriptCode>()
    private val gson = Gson()

    // Optional: install a custom ContextFactory that stops long-running scripts.
    // If you set useTimeout=true, we'll init a custom factory for this engine instance.
    private val localFactory: ContextFactory? = if (useTimeout) {
        object : ContextFactory() {
            // Called to create each Context
            override fun makeContext(): Context {
                val cx = super.makeContext()
                // when the engine executes this many "instructions" Rhino calls observeInstructionCount
                cx.instructionObserverThreshold = instructionLimit
                // Required for Android - disable JIT/optimization
                cx.optimizationLevel = -1
                return cx
            }

            override fun observeInstructionCount(cx: Context?, instructionCount: Int) {
                throw ScriptTimeoutException("JS script exceeded instruction limit ($instructionLimit)")
            }
        }.also { factory ->
            factory
        }
    } else null



    override fun setCodes(codes: List<ScriptCode>) {
        this.codes.clear()
        this.codes.addAll(codes)
    }

    override fun evaluate(screenAnalysis: ScreenAnalysis): ScriptResult {
        val cx: Context = try {
            if (localFactory != null) {
                localFactory.enterContext()
            } else {
                Context.enter()
            }
        } catch (t: Throwable) {
            throw RuntimeException("Failed to init JS engine: ${t.message}", t)
        }.apply {
            optimizationLevel = -1

        }
        return try {
            val scope: Scriptable = cx.initStandardObjects()
            val screenJson = gson.toJson(screenAnalysis)
            cx.evaluateString(
                scope,
                """
    function containsText(node, text) {
        if (!node) return false;
        if (!text) {
          if (screen && screen.hasAppName) return true;
          return false;
        }
        var needle = String(text).toLowerCase();
        function dfs(n) {
          if (!n) return false;
          if (n.text && String(n.text).toLowerCase().indexOf(needle) !== -1) return true;
          if (n.desc && String(n.desc).toLowerCase().indexOf(needle) !== -1) return true;
          if (n.id && String(n.id).toLowerCase().indexOf(needle) !== -1) return true;
          if (n.children && n.children.length) {
            for (var i = 0; i < n.children.length; i++) {
              if (dfs(n.children[i])) return true;
            }
          }
          return false;
        }
        return dfs(node);
    }
    """.trimIndent(),
                "globalHelpers",
                1,
                null
            )


            // Put JSON string and parse it inside the JS context to get a proper JS object
            scope.put("screenJson", scope, Context.javaToJS(screenJson, scope))
            cx.evaluateString(scope, "var screen = JSON.parse(screenJson);", "initScreen", 1, null)
            for (script in codes) {
                val scriptName = script.name
                try {
                    val wrapped = script.code

                    val raw = cx.evaluateString(scope, wrapped, scriptName, 1, null)
                    val boolResult = jsValueToBoolean(raw)
                    if (boolResult) {
                        return ScriptResult.Success(scriptName, true)
                    }
                } catch (e: ScriptTimeoutException) {
                    return ScriptResult.Error("Script '$scriptName' timed out: ${e.message}")
                } catch (e: Throwable) {
                    return ScriptResult.Error("Script '$scriptName' error: ${e.message}")
                }
            }
            // no script returned true
            ScriptResult.Success("", false)
        } catch (e: Throwable) {
            ScriptResult.Error("JS engine error: ${e.message}")
        } finally {
            try {
                Context.exit()
            } catch (e: Throwable) {
                e.printStackTrace()//todo
            }
        }
    }

    private fun jsValueToBoolean(raw: Any?): Boolean {
        return when (raw) {
            null -> false
            is Boolean -> raw
            is Number -> raw.toInt() != 0
            is String -> {
                raw.equals("true", ignoreCase = true)
            }

            else -> false
        }
    }

    class ScriptTimeoutException(message: String) : RuntimeException(message)
}