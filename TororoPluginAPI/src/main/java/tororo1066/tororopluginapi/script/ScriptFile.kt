package tororo1066.tororopluginapi.script

import com.ezylang.evalex.config.ExpressionConfiguration
import tororo1066.tororopluginapi.otherClass.MultipleKeyMap
import tororo1066.tororopluginapi.script.action.*
import tororo1066.tororopluginapi.script.action.hidden.ElseAction
import tororo1066.tororopluginapi.script.action.inline.MathAction
import tororo1066.tororopluginapi.script.expressionFunc.DateFunc
import java.io.File
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ScriptFile(val file: File) {
    val lines = ArrayList<ActionData>()
    val publicVariables = HashMap<String, Any>()
    val breakFunction = HashMap<String, Boolean>()
    val configuration = ExpressionConfiguration.defaultConfiguration()

    init {
        var index = 0
        file.readLines().forEach {
            val script = readScriptLine(it, index)
            if (script == null){
                index--
                return@forEach
            }

            lines.add(script)
            index++
        }
    }

    fun start(){
        lines.forEach {
            if (it.separator == 0){
                it.invoke()
            }
        }
    }

    fun startAsync(): Future<Unit> {
        return Executors.newSingleThreadExecutor().submit(Callable { start() })
    }

    fun readScriptLine(lineStr: String, line: Int): ActionData? {
        var lineString = lineStr
        if (lineString.isBlank() || lineString.startsWith("#"))return null
        var space = 0
        lineString = lineString.dropWhile {
            if (it == ' '){
                space++
                return@dropWhile true
            }
            false
        }
        val actionString = lineString.split(" ")[0]
        val action = actions[actionString.lowercase()]
        if (action == null){
            if (!lineString.contains("="))
                throw NullPointerException("Not found script action or variable function $actionString in ${file.name}(Line: ${line})")
            return ActionData(MathAction(), this, lineString, line, space/2)
        }
        var formatLine = lineStr.replaceFirst("$actionString ", "")
        if (formatLine == lineStr){
            formatLine = lineStr.replaceFirst(actionString, "")
        }
        return ActionData(action, this, formatLine, line, space/2)
    }

    companion object {

        fun actionPair(vararg action: AbstractAction): Map<String,AbstractAction> {
            return action.associateBy { it.internalName }
        }
        val actions = HashMap(actionPair(
            PrintAction(),
            ForAction(),
            IfAction(),
            ElseAction(),
            BreakAction()
        ))
    }

}