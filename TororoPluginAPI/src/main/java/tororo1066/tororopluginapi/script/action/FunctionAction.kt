package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.AbstractFunction
import com.ezylang.evalex.functions.FunctionParameterDefinition
import com.ezylang.evalex.parser.Token
import org.bukkit.Bukkit
import tororo1066.tororopluginapi.script.ScriptFile

class FunctionAction: AbstractAction("def") {

    override fun init(scriptFile: ScriptFile, line: String, lineIndex: Int, separator: Int) {
        val functionName = line.split("(")[0]
        val argsString = line.split("(")[1].split(")")[0].split(",")
        val args = ArrayList<String>()
        Bukkit.broadcastMessage(lineIndex.toString())
        for (arg in argsString) {
            if (arg.isBlank()) {
                continue
            }
            val argName = if (arg.contains(":")) {
                arg.split(":")[0].trim()
            } else {
                arg.trim()
            }
            args.add(argName)
        }
        Bukkit.broadcastMessage("args: ${args.size}")
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
                for ((index, arg) in args.withIndex()) {
                    scriptFile.functionVariables[functionName]!![arg] = parameterValues[index]
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

                if (scriptFile.returns.containsKey(functionName)) {
                    return EvaluationValue(scriptFile.returns[functionName]!!)
                }

                return EvaluationValue(null)
            }
        }
        scriptFile.configuration.functionDictionary.addFunction(functionName, function)
    }

    override fun invoke(scriptFile: ScriptFile, function: String, line: String, lineIndex: Int, separator: Int) {

    }
}