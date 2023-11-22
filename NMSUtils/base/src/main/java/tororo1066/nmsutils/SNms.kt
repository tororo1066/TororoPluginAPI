package tororo1066.nmsutils

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Registry
import org.bukkit.block.Block
import tororo1066.nmsutils.command.AbstractCommandElement

interface SNms {

    fun getMapColor(block: Block): Color

    fun registerCommands(command: String, vararg commands: AbstractCommandElement<*>)

    companion object {

        val version = Bukkit.getServer().bukkitVersion.split("-")[0].replace(".","_")
        fun newInstance() : SNms {
            return newNullableInstance() ?:throw UnsupportedOperationException("SNms not supported mc_version ${Bukkit.getServer().minecraftVersion}.")
        }

        fun newNullableInstance() : SNms? {
            return try {
                val clazz = Class.forName("tororo1066.nmsutils.v${version}.SNmsImpl")
                val instance = clazz.getConstructor().newInstance() as SNms
                instance
            } catch (e: Exception){
                null
            }
        }
    }
}