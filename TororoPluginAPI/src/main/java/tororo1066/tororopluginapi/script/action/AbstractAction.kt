package tororo1066.tororopluginapi.script.action

import tororo1066.tororopluginapi.script.ScriptFile

abstract class AbstractAction(val internalName: String) {

    abstract fun invoke(scriptFile: ScriptFile, line: String, lineIndex: Int, separator: Int)
}