package tororo1066.tororopluginapi.otherClass

open class Dict: HashMapPlus<String,AnyObject>() {

    operator fun set(key: String, value: Any): AnyObject? {
        return put(key, AnyObject(value))
    }

    companion object{
        fun dictOf(vararg pairs: Pair<String, Any>): Dict {
            val dict = Dict()
            pairs.forEach {
                dict[it.first] = AnyObject(it.second)
            }
            return dict
        }
    }
}