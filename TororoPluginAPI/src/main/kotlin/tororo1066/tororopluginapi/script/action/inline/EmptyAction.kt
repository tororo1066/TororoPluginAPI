package tororo1066.tororopluginapi.script.action.inline

import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.script.action.AbstractAction

class EmptyAction: AbstractAction("NONE") {
    override fun invoke(scriptFile: ScriptFile, function: String, line: String, lineIndex: Int, separator: Int) {

    }
}