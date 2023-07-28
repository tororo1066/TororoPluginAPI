package tororo1066.tororopluginapi.otherClass

class MultipleValueMap<K: Any> : HashMapPlus<K, AnyObject>() {

    operator fun set(key: K, value: Any): AnyObject? {
        return put(key, if (value is AnyObject) value else AnyObject(value))
    }
}