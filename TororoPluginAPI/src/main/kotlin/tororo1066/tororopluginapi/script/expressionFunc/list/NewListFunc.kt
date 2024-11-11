package tororo1066.tororopluginapi.script.expressionFunc.list

import com.ezylang.evalex.Expression
import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.AbstractFunction
import com.ezylang.evalex.parser.Token
import tororo1066.tororopluginapi.script.ScriptFile

class NewListFunc(val scriptFile: ScriptFile): AbstractFunction() {

    override fun evaluate(
        expression: Expression,
        functionToken: Token,
        vararg parameterValues: EvaluationValue
    ): EvaluationValue {
        return EvaluationValue.arrayValue(emptyList<Any>())
    }
}