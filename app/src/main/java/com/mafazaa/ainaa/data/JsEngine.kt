package com.mafazaa.ainaa.data

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.mafazaa.ainaa.BuildConfig
import com.mafazaa.ainaa.domain.models.ScreenAnalysis
import com.mafazaa.ainaa.domain.models.ScriptCode
import com.mafazaa.ainaa.domain.models.ScriptResult
import com.mafazaa.ainaa.domain.repo.ScriptRepo
import org.mozilla.javascript.Context
import org.mozilla.javascript.ContextFactory
import org.mozilla.javascript.Scriptable

/**
 * JsEngine is a JavaScript execution engine for evaluating user-provided scripts
 * against a screen analysis model. It uses Mozilla Rhino for JS execution and supports
 * script timeouts and instruction limits for safety.
 *
 * @property useTimeout Whether to enforce a timeout/instruction limit on scripts.
 * @property instructionLimit The maximum number of JS instructions allowed per script.
 *
 * Implements [ScriptRepo] for script management and evaluation.
 */
class JsEngine(
    private val useTimeout: Boolean = true,
    private val instructionLimit: Int = 50_000 // only used when useTimeout==true
) : ScriptRepo {

    private val codes = mutableListOf<ScriptCode>()
    private val gson = Gson()

    /**
     * Optional: Custom ContextFactory to enforce instruction limits and timeouts.
     * If [useTimeout] is true, a custom factory is used for this engine instance.
     */
    private val localFactory: ContextFactory? = if (useTimeout) {
        object : ContextFactory() {
            /**
             * Called to create each Context. Sets instruction observer threshold and disables JIT.
             */
            override fun makeContext(): Context {
                val cx = super.makeContext()
                cx.instructionObserverThreshold = instructionLimit
                cx.optimizationLevel = -1 // Required for Android
                return cx
            }

            /**
             * Called when the instruction limit is reached. Throws a timeout exception.
             */
            override fun observeInstructionCount(cx: Context?, instructionCount: Int) {
                throw ScriptTimeoutException("JS script exceeded instruction limit ($instructionLimit)")
            }
        }.also { factory ->
            factory
        }
    } else null

    /**
     * Sets the list of scripts to be evaluated by this engine.
     * @param codes List of [ScriptCode] objects.
     */
    override fun setCodes(codes: List<ScriptCode>) {
        this.codes.clear()
        this.codes.addAll(codes)
    }

    /**
     * Evaluates all loaded scripts against the provided [ScreenAnalysis].
     * Returns [ScriptResult.Success] if any script returns true, or [ScriptResult.Error] on failure.
     *
     * @param screenAnalysis The screen analysis data to expose to scripts.
     * @return [ScriptResult] indicating the outcome of script evaluation.
     */
    override fun evaluate(screenAnalysis: ScreenAnalysis): ScriptResult {
        val cx: Context? = try {
            if (localFactory != null) {
                localFactory.enterContext()
            } else {
                Context.enter()
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            if (!BuildConfig.DEBUG) {
                FirebaseCrashlytics.getInstance()
                    .log("JsEngine: Context.enter() failed: ${t.message}")
            }
            null
        }?.apply {
            optimizationLevel = -1
        }
        if (cx == null) {
            return ScriptResult.Error("JS engine initialization failed")
        }
        return try {
            // Initialize JS scope and inject helpers and screen data
            val scope: Scriptable = cx.initStandardObjects()
            val screenJson = gson.toJson(screenAnalysis)
            // Injects a helper function for text search in the node tree
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
            // No script returned true
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

    /**
     * Converts a raw JS value to a Boolean for script result evaluation.
     * @param raw The JS value returned by the script.
     * @return True if the value is considered truthy, false otherwise.
     */
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

    /**
     * Exception thrown when a script exceeds the allowed instruction limit.
     */
    class ScriptTimeoutException(message: String) : RuntimeException(message)
}