package tororo1066.tororopluginapi.config.paramConfig

import org.bukkit.Material
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
annotation class Parameter(
    val key: String = "",
    val name: String = "",
    val description: String = "",
    val type: KClass<out AbstractParameterType<*>>,
    val display: Material = Material.PAPER,
)
