package tororo1066.tororopluginapi.script.expressionFunc.list

import com.ezylang.evalex.Expression
import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.AbstractFunction
import com.ezylang.evalex.functions.FunctionParameter
import com.ezylang.evalex.parser.Token
import org.bukkit.Bukkit
import tororo1066.tororopluginapi.script.ScriptFile

@FunctionParameter(name = "list")
@FunctionParameter(name = "condition", isLazy = true)
class FindFunc(val scriptFile: ScriptFile): AbstractFunction() {

    override fun evaluate(
        expression: Expression,
        functionToken: Token,
        vararg parameterValues: EvaluationValue
    ): EvaluationValue {
        val list = parameterValues[0].arrayValue.map { it.value }
        val condition = parameterValues[1].expressionNode
        var result: Any? = null
        for (item in list) {
            val itemData = ScriptFile.miningData(item)
            expression.with("it", itemData)
            val value = expression.evaluateSubtree(condition)
            if (value.booleanValue) {
                result = itemData
                break
            }
        }
        return EvaluationValue(result, scriptFile.configuration)
    }
}