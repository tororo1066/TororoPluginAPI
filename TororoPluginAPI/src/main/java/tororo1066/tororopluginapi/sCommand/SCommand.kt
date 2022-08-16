package tororo1066.tororopluginapi.sCommand

import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.sCommand.report.ReportCommand
import tororo1066.tororopluginapi.sCommand.report.ReportList
import tororo1066.tororopluginapi.sCommand.report.ReportListAll
import tororo1066.tororopluginapi.sCommand.report.ReportLog
import tororo1066.tororopluginapi.sEvent.SEvent
import java.util.function.Consumer
import kotlin.collections.ArrayList

open class SCommand(private val command : String) : CommandExecutor, TabCompleter {


    private var perm : String? = null
    private var prefix = ""

    private val commands = ArrayList<SCommandObject>()

    private var commandNoFoundEvent : Consumer<SCommandData>? = null

    constructor(command : String, prefix : String) : this(command){
        this.prefix = prefix
    }


    constructor(command : String, prefix : String, perm : String) : this(command){
        this.prefix = prefix
        this.perm = perm
    }

    fun setPermission(perm : String){
        this.perm = perm
    }

    private fun register(): PluginCommand? {
        return Bukkit.getPluginCommand(this.command)
    }

    fun addCommand(command : SCommandObject){
        if (this.commands.contains(command))return
        this.commands.add(command)
    }
    fun command(): SCommandObject {
        return SCommandObject()
    }

    fun setCommandNoFoundEvent(event : Consumer<SCommandData>){
        this.commandNoFoundEvent = event
    }

    fun clearCommands(){
        commands.clear()
    }


    init {
        val register = register() ?: throw NullPointerException("\"${command}\"の登録に失敗しました。plugin.ymlを確認してください。")
        register.setExecutor(this)
        register.tabCompleter = this
    }



    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (perm?.let { sender.hasPermission(it) } == false){
            sendPrefixMessage(sender,"§4権限がありません")
            return true
        }
        val commandData = SCommandData(sender, command, label, args)
        for (commandObject in commands){
            if (!commandObject.matches(args)) continue
            if (!commandObject.hasPermission(sender)){
                sendPrefixMessage(sender,"§4権限がありません")
                return true
            }
            if (!commandObject.execute(commandData)) sendPrefixMessage(sender,"§4このコマンドはプレイヤーのみ実行できます")
            return true
        }
        if (this.commandNoFoundEvent != null) this.commandNoFoundEvent!!.accept(commandData)
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        val result = ArrayList<String>()

        for (commandObject in this.commands){
            if (!commandObject.hasPermission(sender)) continue

            if (!commandObject.validOption(args)) continue

            val arg = commandObject.args[args.size-1]

            if (arg.hasType(SCommandArgType.ONLINE_PLAYER)){
                for (p in Bukkit.getOnlinePlayers()){
                    result.add(p.name)
                }
            }

            if (arg.hasType(SCommandArgType.WORLD)){
                for (world in Bukkit.getWorlds()){
                    result.add(world.name)
                }
            }

            if (arg.hasType(SCommandArgType.BOOLEAN)){
                result.add("true")
                result.add("false")
            }
            result.addAll(arg.alias)
        }
        return result
    }


    private fun sendPrefixMessage(p : CommandSender, message : String){
        p.sendMessage(this.prefix + message)
    }

    fun registerReportCommand(plugin: JavaPlugin, reportPerm: String, logPerm: String){
        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("report")).addArg(SCommandArg().addAlias("件名")).addArg(SCommandArg().addAlias("本文")).noLimit(true).addNeedPermission(reportPerm).setExecutor(
            ReportCommand(plugin)
        ))
        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("reportop")).addArg(SCommandArg().addAllowString("list")).setExecutor(ReportList(plugin,logPerm)))
        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("reportop")).addArg(SCommandArg().addAllowString("log")).addArg(SCommandArg().addAlias("ファイル名")).setExecutor(ReportLog(plugin,logPerm)))
        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("reportop")).addArg(SCommandArg().addAllowString("pageList")).addArg(SCommandArg().addAllowType(SCommandArgType.INT).addAlias("ページ")).setExecutor(ReportListAll(plugin,logPerm)))
        if (plugin.description.authors.isEmpty())return
        SEvent(plugin).register(PlayerJoinEvent::class.java){
            for (author in plugin.description.authors){
                if (it.player.uniqueId == Bukkit.getOfflinePlayer(author).uniqueId){
                    Bukkit.dispatchCommand(it.player,"$command reportlist")
                }
            }

        }
    }

}