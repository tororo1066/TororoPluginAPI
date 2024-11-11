package tororo1066.tororopluginapi.sCommand

abstract class ChangeableAllowString {
    abstract fun getAllowString(data: SCommandData): Collection<String>
}