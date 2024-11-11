package tororo1066.tororopluginapi.utils

import org.bukkit.Location
import org.bukkit.block.BlockFace
import kotlin.math.floor
import kotlin.math.roundToInt

fun Location.toLocString(type: LocType): String {
    return when(type){
        LocType.COMMA-> "${this.x},${this.y},${this.z}"
        LocType.SPACE-> "${this.x} ${this.y} ${this.z}"
        LocType.BLOCK_COMMA-> "${this.blockX},${this.blockY},${this.blockZ}"
        LocType.BLOCK_SPACE-> "${this.blockX} ${this.blockY} ${this.blockZ}"
        LocType.WORLD_COMMA-> "${this.world.name},${this.x},${this.y},${this.z}"
        LocType.WORLD_SPACE-> "${this.world.name} ${this.x} ${this.y} ${this.z}"
        LocType.WORLD_BLOCK_COMMA-> "${this.world.name},${this.blockX},${this.blockY},${this.blockZ}"
        LocType.WORLD_BLOCK_SPACE-> "${this.world.name} ${this.blockX} ${this.blockY} ${this.blockZ}"
        LocType.DIR_COMMA-> "${this.x},${this.y},${this.z},${this.yaw},${this.pitch}"
        LocType.DIR_SPACE-> "${this.x} ${this.y} ${this.z} ${this.yaw} ${this.pitch}"
        LocType.DIR_BLOCK_COMMA-> "${this.blockX},${this.blockY},${this.blockZ},${this.yaw.toInt()},${this.pitch.toInt()}"
        LocType.DIR_BLOCK_SPACE-> "${this.blockX} ${this.blockY} ${this.blockZ} ${this.yaw.toInt()} ${this.pitch.toInt()}"
        LocType.ALL_COMMA-> "${this.world.name},${this.x},${this.y},${this.z},${this.yaw},${this.pitch}"
        LocType.ALL_SPACE-> "${this.world.name} ${this.x} ${this.y} ${this.z} ${this.yaw} ${this.pitch}"
        LocType.ALL_BLOCK_COMMA-> "${this.world.name},${this.blockX},${this.blockY},${this.blockZ},${this.yaw.toInt()},${this.pitch.toInt()}"
        LocType.ALL_BLOCK_SPACE-> "${this.world.name} ${this.blockX} ${this.blockY} ${this.blockZ} ${this.yaw.toInt()} ${this.pitch.toInt()}"
    }
}

enum class LocType {
    COMMA,
    SPACE,
    BLOCK_COMMA,
    BLOCK_SPACE,
    WORLD_COMMA,
    WORLD_SPACE,
    WORLD_BLOCK_COMMA,
    WORLD_BLOCK_SPACE,
    DIR_COMMA,
    DIR_SPACE,
    DIR_BLOCK_COMMA,
    DIR_BLOCK_SPACE,
    ALL_COMMA,
    ALL_SPACE,
    ALL_BLOCK_COMMA,
    ALL_BLOCK_SPACE
}

fun Location.addX(x: Double): Location {
    return this.add(x,0.0,0.0)
}

fun Location.addY(y: Double): Location {
    return this.add(0.0,y,0.0)
}

fun Location.addZ(z: Double): Location {
    return this.add(0.0,0.0,z)
}

fun Location.addYaw(yaw: Float): Location {
    this.yaw += yaw
    return this
}

fun Location.addPitch(pitch: Float): Location {
    this.pitch += pitch
    return this
}
fun Location.setXL(x: Double): Location {
    this.x = x
    return this
}

fun Location.setYL(y: Double): Location {
    this.y = y
    return this
}

fun Location.setZL(z: Double): Location {
    this.z = z
    return this
}

fun Location.setYawL(yaw: Float): Location {
    this.yaw = yaw
    return this
}

fun Location.setPitchL(pitch: Float): Location {
    this.pitch = pitch
    return this
}

fun Location.floorXYZ(): Location {
    this.x = floor(this.x)
    this.y = floor(this.y)
    this.z = floor(this.z)
    return this
}