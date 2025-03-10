package tororo1066.tororopluginapi.config.paramConfig

import org.bukkit.entity.Player
import tororo1066.tororopluginapi.SInput
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.sInventory.SInventory
import java.lang.reflect.Field

abstract class AbstractParameterType<T> {

    companion object {
        val sInput = SInput(SJavaPlugin.plugin)
    }

    abstract fun getConfigValue(value: T): Any

    abstract fun getValue(configValue: Any): T

    abstract fun sendUpdate(
        p: Player,
        inventory: SInventory,
        instance: Any,
        field: Field,
        key: String,
        currentValue: T?
    )

    abstract fun getStringInfo(value: T): String

    protected fun Field.setWithAccessible(instance: Any, value: Any) {
        val canAccess = this.canAccess(instance)
        this.isAccessible = true
        if (this.type.isAssignableFrom(List::class.java)) {
            val list = this.get(instance) as List<*>
            this.set(instance, list + value)
        } else {
            this.set(instance, value)
        }
        this.isAccessible = canAccess
    }
}

class StringParameterType : AbstractParameterType<String>() {
    override fun getConfigValue(value: String): Any {
        return value
    }

    override fun getValue(configValue: Any): String {
        return configValue.toString()
    }

    override fun sendUpdate(
        p: Player,
        inventory: SInventory,
        instance: Any,
        field: Field,
        key: String,
        currentValue: String?
    ) {
        inventory.throughClose(p)
        sInput.sendInputCUI(p, String::class.java, "文字列を入力してください", action = {
            field.setWithAccessible(instance, it)
            inventory.open(p)
        }, onCancel = {
            inventory.open(p)
        })
    }

    override fun getStringInfo(value: String): String {
        return value
    }
}

class IntParameterType : AbstractParameterType<Int>() {
    override fun getConfigValue(value: Int): Any {
        return value
    }

    override fun getValue(configValue: Any): Int {
        return (configValue as? Int) ?: configValue.toString().toInt()
    }

    override fun sendUpdate(
        p: Player,
        inventory: SInventory,
        instance: Any,
        field: Field,
        key: String,
        currentValue: Int?
    ) {
        inventory.throughClose(p)
        sInput.sendInputCUI(p, Int::class.java, "整数を入力してください", action = {
            field.setWithAccessible(instance, it)
            inventory.open(p)
        }, onCancel = {
            inventory.open(p)
        })
    }

    override fun getStringInfo(value: Int): String {
        return value.toString()
    }
}

class EnumParameterType<T : Enum<T>>(private val enumClass: Class<T>) : AbstractParameterType<T>() {
    override fun getConfigValue(value: T): Any {
        return value.name
    }

    override fun getValue(configValue: Any): T {
        return enumClass.enumConstants.first { it.name == configValue }
    }

    override fun sendUpdate(
        p: Player,
        inventory: SInventory,
        instance: Any,
        field: Field,
        key: String,
        currentValue: T?
    ) {
        inventory.throughClose(p)
        sInput.sendInputCUI(p, enumClass, "${enumClass.simpleName}を入力してください", action = {
            field.setWithAccessible(instance, it)
            inventory.open(p)
        }, onCancel = {
            inventory.open(p)
        })
    }

    override fun getStringInfo(value: T): String {
        return value.name
    }
}