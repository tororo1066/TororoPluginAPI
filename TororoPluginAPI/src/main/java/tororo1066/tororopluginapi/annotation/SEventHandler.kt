package tororo1066.tororopluginapi.annotation

import org.bukkit.event.EventPriority

@Target(AnnotationTarget.FUNCTION)
annotation class SEventHandler(val property: EventPriority = EventPriority.NORMAL, val autoRegister: Boolean = true)
