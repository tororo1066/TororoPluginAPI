package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import tororo1066.tororopluginapi.script.ScriptFile

class SleepAction: AbstractAction("sleep") {

    override fun invoke(scriptFile: ScriptFile, function: String, line: String, lineIndex: Int, separator: Int) {
        Thread.sleep(Expression(line, scriptFile.configuration)
            .withValues(scriptFile.publicVariables)
            .evaluate().numberValue.toLong())
    }
}