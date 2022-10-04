package tororo1066.tororopluginapi.sCommand

import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SDebug
import tororo1066.tororopluginapi.annotation.SCommandBody
import tororo1066.tororopluginapi.lang.LangEditor
import tororo1066.tororopluginapi.lang.SLang
import java.util.function.Consumer

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

    fun reloadSCommandBodies(){
        clearCommands()
        javaClass.fields.forEach {
            if (it.isAnnotationPresent(SCommandBody::class.java) && it.type == SCommandObject::class.java){
                it.isAccessible = true
                val data = it.get(this) as SCommandObject
                val sCommand = it.getAnnotation(SCommandBody::class.java)
                addCommand(data.addNeedPermission(sCommand.permission))
            }
        }
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
    fun registerDebugCommand(perm: String){
        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("debug")).addArg(SCommandArg().addAllowType(SCommandArgType.INT).addAlias("level"))
            .addNeedPermission(perm)
            .setNormalExecutor {
                if (it.sender is Player){
                    SDebug.debugLevel[it.sender.uniqueId] = it.args[1].toInt()
                } else {
                    SDebug.consoleSenderLevel = it.args[1].toInt()
                }
                it.sender.sendMessage("${it.sender.name} debug level ${it.args[1]} now.")
            })
    }

    fun registerSLangCommand(plugin: JavaPlugin, perm: String){
        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("lang")).addNeedPermission(perm).setNormalExecutor {
            it.sender.sendMessage("§a==================LanguageHelp==================")
            it.sender.sendMessage("§b/${command} lang list §7Show languages list.")
            it.sender.sendMessage("§b/${command} lang default <Language> §7Set default language.")
            it.sender.sendMessage("§b/${command} lang editor §7Open lang editor.")
            it.sender.sendMessage("§a==================LanguageHelp==================")
        })

        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("lang")).addArg(SCommandArg().addAllowString("list")).addNeedPermission(perm).setNormalExecutor {
            if (SLang.langFile.isEmpty()){
                it.sender.sendMessage("$prefix§cLanguages is Empty.")
                return@setNormalExecutor
            } else {
                it.sender.sendMessage("$prefix§aLanguages List")
                SLang.langFile.keys.forEach { lang ->
                    it.sender.sendMessage("$prefix§b$lang")
                }
            }
        })

        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("lang")).addArg(SCommandArg().addAllowString("default")).addArg(SCommandArg().addAllowString(SLang.langFile.keys.toTypedArray()).addAlias("Lang")).addNeedPermission(perm).setNormalExecutor {
            val lang = it.args[2]
            SLang.defaultLanguage = lang
            plugin.config.set("defaultLanguage",lang)
            plugin.saveConfig()
            it.sender.sendMessage("$prefix§aDefault lang is $lang now.")
        })

        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("lang")).addArg(SCommandArg().addAllowString("editor")).addNeedPermission(perm).setPlayerExecutor {
            LangEditor(plugin).open(it.sender)
        })
    }

}