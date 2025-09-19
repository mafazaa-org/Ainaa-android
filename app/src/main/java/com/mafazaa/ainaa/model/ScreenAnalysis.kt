package com.mafazaa.ainaa.model

data class  ScreenAnalysis (
    val pkg: String?,
    val appName : String,
    val nodesCount : Int,
    val root:ScreenNode,
    val hasAppName: Boolean,
    val isSettingsScreen :Boolean
){
    override fun toString(): String {
        return "nodes:$nodesCount, app:$pkg, has our app name:$hasAppName,is a settings screen:$isSettingsScreen"+
                "\n"+ root.toString()
    }
}

data class ScreenNode (
    val cls: String?,
    val text: String?,
    val id: String?,
    val desc: String?,
    val children: List<ScreenNode> = emptyList()
){
    override fun toString(): String {//consider this as root
        return toString(0)
    }
     fun toString(level :Int): String {
        val indent = "  ".repeat(level)
        val sb = StringBuilder()
        sb.append("$indent- cls: $cls, text: $text, id: $id, desc: $desc\n")
        for (child in children) {
            sb.append(child.toString(level + 1))
        }
        return sb.toString()
    }

}
