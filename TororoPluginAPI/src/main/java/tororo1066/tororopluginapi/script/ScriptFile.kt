package tororo1066.tororopluginapi.script

import com.ezylang.evalex.Expression
import com.ezylang.evalex.config.ExpressionConfiguration
import com.ezylang.evalex.functions.AbstractFunction
import org.bukkit.Bukkit
import tororo1066.tororopluginapi.script.action.*
import tororo1066.tororopluginapi.script.action.entity.player.SendMessageAction
import tororo1066.tororopluginapi.script.action.hidden.ElseAction
import tororo1066.tororopluginapi.script.action.inline.EmptyAction
import tororo1066.tororopluginapi.script.action.inline.MathAction
import tororo1066.tororopluginapi.script.expressionFunc.DateFunc
import tororo1066.tororopluginapi.script.expressionFunc.IsOp
import tororo1066.tororopluginapi.script.expressionFunc.list.ContainsFunc
import tororo1066.tororopluginapi.script.expressionFunc.list.FindFunc
import tororo1066.tororopluginapi.script.expressionFunc.list.SizeFunc
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class ScriptFile(val file: File) {
    val lines = ArrayList<ActionData>()
    val publicVariables = HashMap<String, Any?>()
    val breakFunction = HashMap<String, Boolean>()
    val returns = HashMap<String, Any?>()
    val functionVariables = HashMap<String, HashMap<String, Any?>>()

    val configuration = ExpressionConfiguration.defaultConfiguration()
        .apply {
            functionDictionary.apply {
                functions.forEach { (key, value) -> addFunction(key, value.invoke(this@ScriptFile)) }
            }
        }

    var debug = false

    constructor(file: File, debug: Boolean): this(file){
        this.debug = debug
    }

    init {
        var index = 0
        file.readLines().forEach {
            val script = readScriptLine(it, index)

            lines.add(script)
            index++
        }

        lines.forEach {
            it.init()
        }
    }

    fun start(): Any? {
        returns.clear()
        lines.forEach {
            if (it.separator == 0){
                it.invoke("main")
            }
            if (returns.containsKey("main")){
                return returns["main"]
            }
        }

        return Unit
    }

    fun startAsync(): CompletableFuture<Any> {
        return CompletableFuture.supplyAsync({ start() }, Executors.newSingleThreadExecutor())
    }

    fun readScriptLine(lineStr: String, line: Int): ActionData {
        var lineString = lineStr
        if (lineString.isBlank() || lineString.startsWith("#"))return ActionData(EmptyAction(), this, lineString, line, 0)
        var space = 0
        lineString = lineString.dropWhile {
            if (it == ' '){
                space++
                return@dropWhile true
            }
            false
        }
        val actionString = lineString.split(" ")[0]
        val action = actions[actionString]
        if (action == null){
            if (!lineString.contains("="))
                throw NullPointerException("Not found script action or variable function $actionString in ${file.name}(Line: ${line})")
            debug("Loading math action '$lineString' in ${file.name}(Line: ${line})...")
            return ActionData(MathAction(), this, lineString, line, space/2)
        }
        var formatLine = lineString.replaceFirst("$actionString ", "")
        if (formatLine == lineString){
            formatLine = lineString.replaceFirst(actionString, "")
        }
        debug("Loading action '$lineString' in ${file.name}(Line: ${line})...")
        return ActionData(action, this, formatLine, line, space/2)
    }

    fun debug(message: String){
        if (!debug)return
        Bukkit.getLogger().info("[Script Debug] $message")
    }

    companion object {

        val functions = HashMap<String, (ScriptFile) -> AbstractFunction>(
            mapOf(
                "now" to { DateFunc() },
                "isOp" to { IsOp(it) },
                "size" to { SizeFunc() },
                "find" to { FindFunc() },
                "contains" to { ContainsFunc() }
            )
        )

        fun actionPair(vararg action: AbstractAction): Map<String,AbstractAction> {
            return action.associateBy { it.internalName }
        }
        val actions = HashMap(actionPair(
            BroadcastAction(),
            ForAction(),
            WhileAction(),
            IfAction(),
            ElseAction(),
            BreakAction(),
            ReturnAction(),
            SleepAction(),
            SendMessageAction(),
            FunctionAction(),
        ))

        fun Expression.withVariables(function: String, scriptFile: ScriptFile): Expression {
            return apply {
                withValues(scriptFile.publicVariables)
                if (scriptFile.functionVariables[function] != null){
                    withValues(scriptFile.functionVariables[function]!!)
                }
            }

        }
    }

}