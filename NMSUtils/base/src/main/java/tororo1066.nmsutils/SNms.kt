package tororo1066.nmsutils

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.Plugin

interface SNms {

    fun getMapColor(block: Block): Color

    companion object{
        fun newInstance() : SNms {
            return newNullableInstance()?:throw UnsupportedOperationException("SNms not supported mc_version ${Bukkit.getServer().minecraftVersion}.")
        }

        fun newNullableInstance() : SNms? {
            return try {
                val version = Bukkit.getServer().bukkitVersion.split("-")[0].replace(".","_")
                val clazz = Class.forName("tororo1066.nmsutils.v${version}.SNmsImpl")
                val instance = clazz.getConstructor().newInstance() as SNms
                instance
            } catch (e: Exception){
                null
            }
        }
    }
}