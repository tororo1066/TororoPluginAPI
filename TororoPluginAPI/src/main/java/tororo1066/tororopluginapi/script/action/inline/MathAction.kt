package tororo1066.tororopluginapi.script.action.inline

import com.ezylang.evalex.Expression
import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.script.ScriptFile.Companion.withVariables
import tororo1066.tororopluginapi.script.action.AbstractAction
import java.math.BigDecimal

class MathAction: AbstractAction("NONE") {

    val checkList = listOf('+', '-', '*', '/', '=')

    override fun invoke(scriptFile: ScriptFile, function: String, line: String, lineIndex: Int, separator: Int) {
        var variableName = ""
        val (sign, expr) = line.dropWhile {
            if (checkList.contains(it))return@dropWhile false
            if (it != ' '){
                variableName += it
            }
            true
        }.split(" ")
        val value = Expression(expr)
            .withVariables(function, scriptFile)
            .evaluate().value
        when(sign){
            "+=" -> {
                val beforeValue = scriptFile.publicVariables[variableName]!!
                when(value){
                    is BigDecimal -> {
                        scriptFile.publicVariables[variableName] = (beforeValue as BigDecimal) + value
                    }
                    is String -> {
                        scriptFile.publicVariables[variableName] = (beforeValue as String) + value
                    }
                    else -> {
                        throw UnsupportedOperationException("$sign is not supported value type ${value.javaClass.simpleName}")
                    }
                }
            }

            "-=" -> {
                val beforeValue = scriptFile.publicVariables[variableName]!!
                when(value){
                    is BigDecimal -> {
                        scriptFile.publicVariables[variableName] = (beforeValue as BigDecimal) - value
                    }
                    else -> {
                        throw UnsupportedOperationException("$sign is not supported value type ${value.javaClass.simpleName}")
                    }
                }
            }

            "/=" -> {
                val beforeValue = scriptFile.publicVariables[variableName]!!
                when(value){
                    is BigDecimal -> {
                        scriptFile.publicVariables[variableName] = (beforeValue as BigDecimal) / value
                    }
                    else -> {
                        throw UnsupportedOperationException("$sign is not supported value type ${value.javaClass.simpleName}")
                    }
                }
            }

            "*=" -> {
                val beforeValue = scriptFile.publicVariables[variableName]!!
                when(value){
                    is BigDecimal -> {
                        scriptFile.publicVariables[variableName] = (beforeValue as BigDecimal) * value
                    }
                    else -> {
                        throw UnsupportedOperationException("$sign is not supported value type ${value.javaClass.simpleName}")
                    }
                }
            }

            "=" -> {
                scriptFile.publicVariables[variableName] = value
            }
        }
    }
}