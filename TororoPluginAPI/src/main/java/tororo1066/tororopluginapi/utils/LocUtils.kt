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