package tororo1066.tororopluginapi.annotation

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class SCommandV2Body(val asRoot: Boolean = false)
