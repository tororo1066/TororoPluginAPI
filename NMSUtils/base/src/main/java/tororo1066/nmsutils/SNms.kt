package tororo1066.nmsutils

import com.mojang.brigadier.Message
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.block.Block
import tororo1066.commandapi.SCommandV2Literal

interface SNms {

    fun getMapColor(block: Block): Color

    fun registerCommand(command: SCommandV2Literal)

    fun translate(text: String, vararg variable: Any): Message
}