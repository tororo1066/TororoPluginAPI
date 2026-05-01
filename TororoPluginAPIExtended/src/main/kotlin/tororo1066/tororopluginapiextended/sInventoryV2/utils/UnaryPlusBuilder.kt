package tororo1066.tororopluginapiextended.sInventoryV2.utils

class UnaryPlusBuilder<T: Any> {
    private val list = mutableListOf<T>()

    operator fun T.unaryPlus() {
        list.add(this)
    }

    fun build(): List<T> {
        return list
    }
}