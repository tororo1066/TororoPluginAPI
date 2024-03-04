package tororo1066.tororopluginapi.script.action

import tororo1066.tororopluginapi.script.ScriptFile

class BreakAction: AbstractAction("break") {

    override fun invoke(scriptFile: ScriptFile, function: String, line: String, lineIndex: Int, separator: Int) {
        val replaceSpace = line.replace(" ","")
        if (replaceSpace.isBlank()){
            scriptFile.breakFunction[(scriptFile.breakFunction.entries.lastOrNull()?:return).key] = true
            return
        }
    }
}