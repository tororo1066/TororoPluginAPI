package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import tororo1066.tororopluginapi.script.ScriptFile

class SleepAction: AbstractAction("sleep") {

    override fun invoke(scriptFile: ScriptFile, line: String, lineIndex: Int, separator: Int) {
        Thread.sleep(Expression(line, ScriptFile.configuration)
            .withValues(scriptFile.publicVariables)
            .evaluate().numberValue.toLong())
    }
}