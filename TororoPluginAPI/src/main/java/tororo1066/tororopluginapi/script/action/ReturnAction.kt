package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import tororo1066.tororopluginapi.script.ScriptFile

class ReturnAction: AbstractAction("return") {

    override fun invoke(scriptFile: ScriptFile, function: String, line: String, lineIndex: Int, separator: Int) {
        if (line.isBlank()){
            scriptFile.returns[function] = Unit
            return
        }
        val expression = Expression(line, scriptFile.configuration)
            .withValues(scriptFile.publicVariables)
        scriptFile.returns[function] = expression.evaluate().value
    }
}