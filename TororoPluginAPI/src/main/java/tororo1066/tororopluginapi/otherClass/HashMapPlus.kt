package tororo1066.tororopluginapi.otherClass

open class HashMapPlus<K: Any, V: Any>: HashMap<K, V>() {

    override fun get(key: K): V {
        return super.get(key)!!
    }

    open fun getNullable(key: K): V? {
        return super.get(key)
    }

    open fun deepClone(): HashMapPlus<K, V> {
        val map = HashMapPlus<K, V>()
        forEach {
            var copyKey = it.key
            if (it.key is Cloneable) {
                val method = it.key::class.java.getDeclaredMethod("clone")
                method.isAccessible = true
                copyKey = method.invoke(it.key) as? K?:it.key
                method.isAccessible = false
            }

            var copyValue = it.value
            if (it.value is Cloneable) {
                val method = it.value::class.java.getDeclaredMethod("clone")
                method.isAccessible = true
                copyValue = method.invoke(it.value) as? V?:it.value
                method.isAccessible = false
            }

            map[copyKey] = copyValue
        }
        return map
    }
}