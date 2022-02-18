package tororo1066.tororopluginapi.sCommand.report

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.entity.SPlayer
import tororo1066.tororopluginapi.sCommand.OnlyPlayerExecutor
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ReportCommand(val plugin: JavaPlugin) : OnlyPlayerExecutor {
    override fun onCommand(sender: SPlayer, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size < 3){
            sender.sendMessage("§a/${label} report <件名(20文字以内)> <内容(300文字以内、スペース使用可)>")
            return true
        }
        if (args[1].length > 20){
            sender.sendMessage("§4件名は20文字以内で入力してください")
            return true
        }

        if (args[2].length > 300){
            sender.sendMessage("§4内容は300文字以内で入力してください")
            return true
        }
        val file = File(plugin.dataFolder.path + "/report")
        if (!file.exists()) file.mkdir()
        val now = Date()
        val createFile = File(file.path + "/${sender.name}_${args[1]}_${SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(now)}.yml")
        if (createFile.exists()){
            sender.sendMessage("Error")
            return true
        }
        createFile.createNewFile()
        val yaml = YamlConfiguration.loadConfiguration(createFile)
        yaml.set("Player",sender.name)
        yaml.set("UUID",sender.uniqueId.toString())
        yaml.set("Title",args[1])
        yaml.set("Date",SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now))
        yaml.set("Text",args.joinToString(" ").replaceFirst("${args[0]} ${args[1]} ",""))
        yaml.set("Read",false)
        yaml.save(createFile)
        sender.sendMessage("§a報告が完了しました！")

        if (plugin.description.authors.isEmpty())return true
        plugin.description.authors.forEach {
            val player = Bukkit.getPlayer(it)
            player?.spigot()?.sendMessage(
                *ComponentBuilder("§b§l${plugin.name}§aで§d${sender.name}§aからreportが来ています！(§e件名：${args[1]}§a)")
                    .event(ClickEvent(ClickEvent.Action.RUN_COMMAND,"/${label} reportlog ${createFile.nameWithoutExtension}"))
                    .event(HoverEvent(HoverEvent.Action.SHOW_TEXT,Text("§6ここをクリックでログを見る"))).create())
        }
        return true
    }
}