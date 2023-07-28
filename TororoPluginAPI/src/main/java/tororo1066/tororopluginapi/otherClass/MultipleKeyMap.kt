package tororo1066.tororopluginapi.otherClass

open class MultipleKeyMap<V: Any> : HashMapPlus<AnyObject, V>() {

    operator fun set(key: Any, value: V): V? {
        return put(if (key is AnyObject) key else AnyObject(key), value)
    }

    operator fun get(key: Any): V? {
        return getNullable(if (key is AnyObject) key else AnyObject(key))
    }

    open fun remove(key: Any): V? {
        return remove(if (key is AnyObject) key else AnyObject(key))
    }


}