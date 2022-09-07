package tororo1066.tororopluginapi.utils

import org.bukkit.Location

fun Location.toLocString(type: LocType): String {
    return when(type){
        LocType.COMMA-> "${this.x},${this.y},${this.x}"
        LocType.SPACE-> "${this.x} ${this.y} ${this.x}"
        LocType.BLOCK_COMMA-> "${this.blockX},${this.blockY},${this.blockZ}"
        LocType.BLOCK_SPACE-> "${this.blockX} ${this.blockY} ${this.blockZ}"
    }
}

enum class LocType {
    COMMA,
    SPACE,
    BLOCK_COMMA,
    BLOCK_SPACE
}