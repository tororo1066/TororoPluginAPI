package tororo1066.tororopluginapi.script

import tororo1066.tororopluginapi.script.action.AbstractAction

data class ActionData(val action: AbstractAction, val scriptFile: ScriptFile, val line: String, val lineIndex: Int, val separator: Int) {

    fun init() {
        action.init(scriptFile, line, lineIndex, separator)
    }

    fun invoke(function: String){
        scriptFile.debug("Invoke ${action.internalName} at line $lineIndex (line: $line, separator: $separator)")
        action.invoke(scriptFile, function, line, lineIndex, separator)
    }
}