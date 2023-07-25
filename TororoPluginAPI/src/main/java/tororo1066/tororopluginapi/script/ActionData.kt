package tororo1066.tororopluginapi.script

import tororo1066.tororopluginapi.script.action.AbstractAction

data class ActionData(val action: AbstractAction, val scriptFile: ScriptFile, val line: String, val lineIndex: Int, val separator: Int) {

    fun invoke(){
        action.invoke(scriptFile, line, lineIndex, separator)
    }
}