package tororo1066.tororopluginapi.script.expressionFunc.list

import com.ezylang.evalex.Expression
import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.AbstractFunction
import com.ezylang.evalex.functions.FunctionParameter
import com.ezylang.evalex.parser.Token
import org.bukkit.Bukkit

@FunctionParameter(name = "list")
@FunctionParameter(name = "condition", isLazy = true)
class FindFunc: AbstractFunction() {

    override fun evaluate(
        expression: Expression,
        functionToken: Token,
        vararg parameterValues: EvaluationValue
    ): EvaluationValue {
        val list = parameterValues[0].arrayValue
        val condition = parameterValues[1].expressionNode
        var result: Any? = null
        for (item in list) {
            expression.with("it", item.value)
            val value = expression.evaluateSubtree(condition)
            if (value.booleanValue) {
                result = item.value
                break
            }
        }
        return EvaluationValue(result)
    }
}