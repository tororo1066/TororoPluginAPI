package tororo1066.tororopluginapi.script.expressionFunc

import com.ezylang.evalex.Expression
import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.AbstractFunction
import com.ezylang.evalex.functions.FunctionParameter
import com.ezylang.evalex.parser.Token
import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.script.action.AbstractAction

@FunctionParameter(name = "player")
class IsOp(val scriptFile: ScriptFile): AbstractFunction() {

    override fun evaluate(
        expression: Expression,
        functionToken: Token,
        vararg parameterValues: EvaluationValue
    ): EvaluationValue {
        return EvaluationValue(
            AbstractAction.getPlayer(parameterValues[0].stringValue, scriptFile)?.isOp?:false,
            expression.configuration
        )
    }
}