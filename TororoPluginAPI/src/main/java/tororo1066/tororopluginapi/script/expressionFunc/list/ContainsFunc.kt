package tororo1066.tororopluginapi.script.expressionFunc.list

import com.ezylang.evalex.Expression
import com.ezylang.evalex.functions.AbstractFunction
import com.ezylang.evalex.functions.FunctionParameter
import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.parser.Token

@FunctionParameter(name = "list")
@FunctionParameter(name = "value")
class ContainsFunc: AbstractFunction() {

    override fun evaluate(
        expression: Expression,
        functionToken: Token,
        vararg parameterValues: EvaluationValue
    ): EvaluationValue {
        val list = parameterValues[0].arrayValue
        val value = parameterValues[1].value
        return EvaluationValue(list.any { it.value == value })
    }
}