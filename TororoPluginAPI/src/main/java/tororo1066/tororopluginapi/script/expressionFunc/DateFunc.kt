package tororo1066.tororopluginapi.script.expressionFunc

import com.ezylang.evalex.Expression
import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.AbstractFunction
import com.ezylang.evalex.parser.Token
import java.util.Date

class DateFunc: AbstractFunction() {

    override fun evaluate(
        expression: Expression,
        functionToken: Token,
        vararg parameterValues: EvaluationValue
    ): EvaluationValue {
        return EvaluationValue(Date().time)
    }
}