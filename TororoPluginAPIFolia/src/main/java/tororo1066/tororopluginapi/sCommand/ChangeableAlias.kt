package tororo1066.tororopluginapi.sCommand

abstract class ChangeableAlias {
    abstract fun getAlias(data: SCommandData): Collection<String>
}