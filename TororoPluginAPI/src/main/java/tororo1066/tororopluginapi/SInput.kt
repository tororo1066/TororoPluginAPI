package tororo1066.tororopluginapi

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.otherClass.PlusInt
import tororo1066.tororopluginapi.otherClass.PlusInt.Companion.toPlusInt
import tororo1066.tororopluginapi.otherClass.StrExcludeFileIllegalCharacter
import tororo1066.tororopluginapi.otherUtils.UsefulUtility
import tororo1066.tororopluginapi.sEvent.SEvent
import tororo1066.tororopluginapi.utils.toIntProgressionOrNull
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Consumer
import kotlin.random.Random

class SInput(private val plugin: JavaPlugin) {

    private fun sendInputCUI0(
        p: Player,
        type: Class<*>,
        message: String = "§a/<入れるデータ(${type.simpleName})>\n§a/cancelでキャンセル",
        action: Consumer<String>) {
        p.sendMessage(message)
        SEvent(plugin).biRegister(PlayerCommandPreprocessEvent::class.java) { cEvent, unit ->
            if (cEvent.player != p) return@biRegister
            cEvent.isCancelled = true

            if (cEvent.message == "/cancel"){
                p.sendMessage("§aCancelled Input")
                unit.unregister()
                return@biRegister
            }
            val msg = cEvent.message.replaceFirst("/", "")
            action.accept(msg)

            unit.unregister()
        }
    }

    fun <T>sendNullableInputCUI(
        p: Player,
        type: Class<T>,
        message: String = "§a/<入れるデータ(${type.simpleName})>\n§a/cancelでキャンセル",
        errorMsg: (String) -> String = { "§d${it}§4は§d${type.simpleName}§4ではありません" },
        action: Consumer<T?>
    ) {
        sendInputCUI0(p, type, message, Consumer {
            val (blank, value) = modifyClassValue(type, it, allowEmpty = true)
            if (!blank && value == null) {
                p.sendMessage(errorMsg.invoke(it))
                return@Consumer
            }
            action.accept(value)
        })
    }

    fun <T>sendInputCUI(
        p: Player,
        type: Class<T>,
        message: String = "§a/<入れるデータ(${type.simpleName})>\n§a/cancelでキャンセル",
        errorMsg: (String) -> String = { "§d${it}§4は§d${type.simpleName}§4ではありません" },
        action: Consumer<T>
    ) {
        sendInputCUI0(p, type, message, Consumer {
            val (_, value) = modifyClassValue(type, it)
            if (value == null) {
                p.sendMessage(errorMsg.invoke(it))
                return@Consumer
            }
            action.accept(value)
        })
    }

    fun clickAccept(p: Player, message: String, action: ()->Unit, fail: ()->Unit, timeSecond: Int){
        val randomCommand = Random.nextInt(-90000000,90000000)
        var unregistered = false
        p.sendMessage(Component.text(message).clickEvent(ClickEvent.runCommand("/${randomCommand}")))
        val event = SEvent(plugin).biRegister(PlayerCommandPreprocessEvent::class.java) { cEvent, unit ->
            if (cEvent.player != p || cEvent.message.replaceFirst("/","") != randomCommand.toString())return@biRegister
            cEvent.isCancelled = true

            action.invoke()
            unregistered = true
            unit.unregister()
        }
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (!unregistered)return@Runnable
            fail.invoke()
            event.unregister()
        },timeSecond * 20L)
    }


    companion object{
        @Suppress("UNCHECKED_CAST")
        fun <T>modifyClassValue(clazz: Class<T>, value: String, allowEmpty: Boolean = false) : Pair<Boolean, T?> {

            fun returnNull(): Pair<Boolean, T?> {
                return false to null
            }

            if (allowEmpty && value.isBlank())return true to null
            when(clazz){
                String::class.java,java.lang.String::class.java -> {
                    return true to clazz.cast(value)
                }
                SStr::class.java-> {
                    return true to clazz.cast(SStr(value)) as T
                }
                Int::class.java,java.lang.Integer::class.java -> {
                    val int = value.toIntOrNull()?:return returnNull()
                    return true to int as T
                }
                Double::class.java,java.lang.Double::class.java -> {
                    val double = value.toDoubleOrNull()?:return returnNull()
                    return true to double as T
                }
                Long::class.java,java.lang.Long::class.java -> {
                    val long = value.toLongOrNull()?:return returnNull()
                    return true to long as T
                }
                Boolean::class.java,java.lang.Boolean::class.java->{
                    if (value != "true" && value != "false")return returnNull()
                    return true to value.toBoolean() as T
                }
                Player::class.java -> {
                    val player = Bukkit.getPlayer(value)?:return returnNull()
                    return true to player as T
                }
                Location::class.java -> {
                    val split = value.split(" ")

                    return when(split.size){
                        3->{
                            true to clazz.cast(Location(null,split[0].toDoubleOrNull()?:return returnNull(),split[1].toDoubleOrNull()?:return returnNull(),split[2].toDoubleOrNull()?:return returnNull())) as T
                        }

                        4->{
                            true to clazz.cast(Location(Bukkit.getWorld(split[0])?:return returnNull(),split[1].toDoubleOrNull()?:return returnNull(),split[2].toDoubleOrNull()?:return returnNull(),split[3].toDoubleOrNull()?:return returnNull())) as T
                        }

                        5->{
                            true to clazz.cast(Location(null,split[0].toDoubleOrNull()?:return returnNull(),split[1].toDoubleOrNull()?:return returnNull(),split[2].toDoubleOrNull()?:return returnNull(),split[3].toFloatOrNull()?:return returnNull(),split[4].toFloatOrNull()?:return returnNull())) as T
                        }

                        6->{
                            true to clazz.cast(Location(Bukkit.getWorld(split[0])?:return returnNull(),split[1].toDoubleOrNull()?:return returnNull(),split[2].toDoubleOrNull()?:return returnNull(),split[3].toDoubleOrNull()?:return returnNull(),split[4].toFloatOrNull()?:return returnNull(),split[5].toFloatOrNull()?:return returnNull())) as T
                        }

                        else->{
                            returnNull()
                        }
                    }
                }
                PlusInt::class.java -> {
                    val int = value.toPlusInt()?:return returnNull()
                    return true to clazz.cast(int) as T
                }
                BlockFace::class.java -> {
                    val face = UsefulUtility.sTry({BlockFace.valueOf(value.uppercase())}) { null } ?:return returnNull()
                    return true to clazz.cast(face) as T
                }
                World::class.java -> {
                    val world = Bukkit.getWorld(value)?:return returnNull()
                    return true to world as T
                }
                Date::class.java->{
                    val date = UsefulUtility.sTry({SimpleDateFormat("yyyy/MM/dd kk:mm:ss").parse(value)},{
                        UsefulUtility.sTry({SimpleDateFormat("yyyy/MM/dd kk:mm").parse(value)}) {
                            UsefulUtility.sTry({ SimpleDateFormat("yyyy/MM/dd kk").parse(value)}) {
                                UsefulUtility.sTry({ SimpleDateFormat("yyyy/MM/dd").parse(value)}) {
                                    UsefulUtility.sTry({ SimpleDateFormat("yyyy/MM").parse(value)}) {
                                        UsefulUtility.sTry({ SimpleDateFormat("yyyy").parse(value)}) {
                                            null
                                        }
                                    }
                                }
                            }
                        }
                    })
                    return (date as? T)?.let { true to it }?:returnNull()
                }
                IntProgression::class.java->{
                    return (value.toIntProgressionOrNull() as? T)?.let { true to it }?:returnNull()
                }
                StrExcludeFileIllegalCharacter::class.java->{
                    if (value.matches(Regex("[(<|>:?\"/\\\\)*]")))return returnNull()
                    return true to StrExcludeFileIllegalCharacter(value) as T
                }

            }

            if (clazz.isEnum){
                val enum = UsefulUtility.sTry({clazz.getMethod("valueOf",String::class.java).invoke(null,value)},{
                    UsefulUtility.sTry({clazz.getMethod("valueOf",String::class.java).invoke(null,value.uppercase())}) {
                        null
                    }
                })
                return (enum as? T)?.let { true to it }?:returnNull()
            }
            return returnNull()
        }
    }
}