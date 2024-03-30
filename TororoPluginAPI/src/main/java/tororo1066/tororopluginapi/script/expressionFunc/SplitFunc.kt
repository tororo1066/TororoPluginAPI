package tororo1066.tororopluginapi.script.expressionFunc

import com.ezylang.evalex.Expression
import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.AbstractFunction
import com.ezylang.evalex.functions.FunctionParameter
import com.ezylang.evalex.parser.Token
import tororo1066.tororopluginapi.script.ScriptFile

@FunctionParameter(name = "string")
@FunctionParameter(name = "separator")
class SplitFunc(val scriptFile: ScriptFile): AbstractFunction() {

    override fun evaluate(
        expression: Expression,
        functionToken: Token,
        vararg parameterValues: EvaluationValue
    ): EvaluationValue {
        return EvaluationValue(parameterValues[0].stringValue.split(parameterValues[1].stringValue), scriptFile.configuration)
    }
}