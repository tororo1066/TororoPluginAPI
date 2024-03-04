package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.AbstractFunction
import com.ezylang.evalex.functions.FunctionParameterDefinition
import com.ezylang.evalex.parser.Token
import tororo1066.tororopluginapi.script.ScriptFile

class FunctionAction: AbstractAction("def") {

    override fun init(scriptFile: ScriptFile, line: String, lineIndex: Int, separator: Int) {
        val functionName = line.split("(")[0]
        val argsString = line.split("(")[1].split(")")[0].split(",")
        val args = ArrayList<String>()
        for (arg in argsString) {
            val argName = if (arg.contains(":")) {
                arg.split(":")[0].trim()
            } else {
                arg.trim()
            }
            args.add(argName)
        }
        val lines = loadNextLines(scriptFile, lineIndex, separator)
        val function = object : AbstractFunction() {

            override fun getFunctionParameterDefinitions(): MutableList<FunctionParameterDefinition> {
                val list = mutableListOf<FunctionParameterDefinition>()
                for (arg in args) {
                    list.add(FunctionParameterDefinition.builder().name(arg).isLazy(true).build())
                }
                return list
            }
            override fun evaluate(
                expression: Expression,
                functionToken: Token,
                vararg parameterValues: EvaluationValue
            ): EvaluationValue {
                scriptFile.functionVariables[functionName] = hashMapOf()
                for (i in args.indices) {
                    scriptFile.functionVariables[functionName]!![args.elementAt(i)] = parameterValues[i].value
                }

                lines.forEach {
                    if (scriptFile.returns.containsKey(functionName)) {
                        return EvaluationValue(scriptFile.returns[functionName]!!)
                    }
                    if (it.separator != separator + 1) {
                        return@forEach
                    }
                    it.invoke(functionName)
                }

                return EvaluationValue(null)
            }
        }
        scriptFile.configuration.functionDictionary.addFunction(functionName, function)
    }

    override fun invoke(scriptFile: ScriptFile, function: String, line: String, lineIndex: Int, separator: Int) {

    }
}