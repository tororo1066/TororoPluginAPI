package tororo1066.tororopluginapi.otherClass

open class Dict<V: Any>: HashMapPlus<String,V>() {

    companion object{
        fun<V: Any> dictOf(vararg pairs: Pair<String, V>): Dict<V> {
            val dict = Dict<V>()
            pairs.forEach {
                dict[it.first] = it.second
            }
            return dict
        }
    }
}