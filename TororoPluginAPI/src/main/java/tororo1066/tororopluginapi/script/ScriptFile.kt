package tororo1066.tororopluginapi.script

import org.bukkit.Bukkit
import tororo1066.tororopluginapi.script.action.AbstractAction
import tororo1066.tororopluginapi.script.action.ForAction
import tororo1066.tororopluginapi.script.action.PrintAction
import tororo1066.tororopluginapi.script.action.inline.MathAction
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ScriptFile(val file: File) {
    val lines = ArrayList<ActionData>()
    val publicVariables = HashMap<String, Any>()

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
//            ?: throw NullPointerException("Not found script action $actionString in ${file.name}(Line: ${line})")
        if (action == null){
            if (!lineString.contains("="))
                throw NullPointerException("Not found script action or variable function $actionString in ${file.name}(Line: ${line})")
            return ActionData(MathAction(), this, lineString, line, space/2)
        }
        return ActionData(action, this, lineString.replace("$actionString ", ""), line, space/2)
    }

    companion object {

        fun actionPair(vararg action: AbstractAction): Map<String,AbstractAction> {
            return action.associateBy { it.internalName }
        }
        val actions = HashMap(actionPair(
            PrintAction(),
            ForAction()
        ))
    }

}