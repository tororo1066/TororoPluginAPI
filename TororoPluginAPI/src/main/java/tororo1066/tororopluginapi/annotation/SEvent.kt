package tororo1066.tororopluginapi.annotation

import org.bukkit.event.EventPriority

@Target(AnnotationTarget.FUNCTION)
annotation class SEvent(val property: EventPriority = EventPriority.NORMAL,val autoRegister: Boolean = true)
