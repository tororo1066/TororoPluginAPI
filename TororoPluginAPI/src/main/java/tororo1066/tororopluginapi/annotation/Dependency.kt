package tororo1066.tororopluginapi.annotation

@Target(AnnotationTarget.CLASS)
annotation class Dependency(
    val group: String,
    val artifact: String,
    val version: String,
    val repository: String = "repo1.maven.org/maven2"
)
