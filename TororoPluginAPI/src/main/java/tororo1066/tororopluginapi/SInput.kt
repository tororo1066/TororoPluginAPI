package tororo1066.tororopluginapi

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.integer.PlusInt
import tororo1066.tororopluginapi.integer.PlusInt.Companion.toPlusInt
import tororo1066.tororopluginapi.otherUtils.UsefulUtility
import tororo1066.tororopluginapi.sEvent.SEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.function.Consumer

class SInput(private val plugin: JavaPlugin) {

    fun <T>sendInputCUI(p: Player, type: Class<T>, message: String, action: Consumer<T>, errorMsg: (String) -> String) {
        p.sendMessage(message)
        SEvent(plugin).biRegister(PlayerCommandPreprocessEvent::class.java) { cEvent, unit ->
            if (cEvent.player != p) return@biRegister
            cEvent.isCancelled = true

            if (cEvent.message == "/cancel"){
                p.sendMessage("§a入力をキャンセルしました")
                unit.unregister()
                return@biRegister
            }
            val msg = cEvent.message.replaceFirst("/", "")
            val modifyValue = modifyClassValue(type, msg)
            if (modifyValue == null) {
                p.sendMessage(errorMsg.invoke(msg))
                return@biRegister
            }

            action.accept(modifyValue)
            unit.unregister()
        }
    }

    fun <T>sendInputCUI(p: Player, type: Class<T>, message: String, action: Consumer<T>) {
        sendInputCUI(p, type, message, action) { "§d${it}§4は§d${type.simpleName}§4ではありません" }
    }

    fun <T>sendInputCUI(p: Player, type: Class<T>, action: Consumer<T>) {
        sendInputCUI(p, type, "§a/<入れるデータ(${type.simpleName})>\n§a/cancelでキャンセル", action)
    }


    companion object{
        @Suppress("UNCHECKED_CAST")
        fun <T>modifyClassValue(clazz: Class<T>, value: String) : T?{
            when(clazz){
                String::class.java,java.lang.String::class.java -> {
                    if (value.isBlank())return null
                    return clazz.cast(value)
                }
                SStr::class.java-> {
                    if (value.isBlank())return null
                    return clazz.cast(SStr(value)) as T
                }
                Int::class.java,java.lang.Integer::class.java -> {
                    val int = value.toIntOrNull()?:return null
                    return int as T
                }
                Double::class.java,java.lang.Double::class.java -> {
                    val double = value.toDoubleOrNull()?:return null
                    return double as T
                }
                Long::class.java,java.lang.Long::class.java -> {
                    val long = value.toLongOrNull()?:return null
                    return long as T
                }
                Boolean::class.java,java.lang.Boolean::class.java->{
                    if (value != "true" && value != "false")return null
                    return value.toBoolean() as T
                }
                Player::class.java -> {
                    val player = Bukkit.getPlayer(value)?:return null
                    return clazz.cast(player) as T
                }
                Location::class.java -> {
                    val split = value.split(" ")

                    return when(split.size){
                        3->{
                            clazz.cast(Location(null,split[0].toDoubleOrNull()?:return null,split[1].toDoubleOrNull()?:return null,split[2].toDoubleOrNull()?:return null)) as T
                        }

                        4->{
                            clazz.cast(Location(Bukkit.getWorld(split[0])?:return null,split[1].toDoubleOrNull()?:return null,split[2].toDoubleOrNull()?:return null,split[3].toDoubleOrNull()?:return null)) as T
                        }

                        5->{
                            clazz.cast(Location(null,split[0].toDoubleOrNull()?:return null,split[1].toDoubleOrNull()?:return null,split[2].toDoubleOrNull()?:return null,split[3].toFloatOrNull()?:return null,split[4].toFloatOrNull()?:return null)) as T
                        }

                        6->{
                            clazz.cast(Location(Bukkit.getWorld(split[0])?:return null,split[1].toDoubleOrNull()?:return null,split[2].toDoubleOrNull()?:return null,split[3].toDoubleOrNull()?:return null,split[4].toFloatOrNull()?:return null,split[5].toFloatOrNull()?:return null)) as T
                        }

                        else->{
                            null
                        }
                    }
                }
                PlusInt::class.java -> {
                    val int = value.toPlusInt()?:return null
                    return clazz.cast(int) as T
                }
                BlockFace::class.java -> {
                    val face = UsefulUtility.sTry({BlockFace.valueOf(value.uppercase())}) { null } ?:return null
                    return clazz.cast(face) as T
                }
                World::class.java -> {
                    val world = Bukkit.getWorld(value)?:return null
                    return world as T
                }
                Date::class.java->{
                    val date = UsefulUtility.sTry({SimpleDateFormat("yyyy/MM/dd/mm/ss").parse(value)},{
                        UsefulUtility.sTry({SimpleDateFormat("yyyy/MM/dd/mm").parse(value)}) {
                            UsefulUtility.sTry({ SimpleDateFormat("yyyy/MM/dd").parse(value)}) {
                                UsefulUtility.sTry({ SimpleDateFormat("yyyy/MM").parse(value)}) {
                                    UsefulUtility.sTry({ SimpleDateFormat("yyyy").parse(value)}) {
                                        null
                                    }
                                }
                            }
                        }
                    })
                    return date as? T
                }

            }
            return null
        }
    }
}