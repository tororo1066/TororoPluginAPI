package tororo1066.nmsutils.v1_19_2

import org.bukkit.Color
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock
import tororo1066.nmsutils.SNms

class SNmsImpl: SNms {

    override fun getMapColor(block: Block): Color {
        val craftBlock = block as CraftBlock
        return Color.fromRGB(craftBlock.nms.getMapColor(craftBlock.handle,craftBlock.position).col)
    }
}