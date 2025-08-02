package tororo1066.tororopluginapi.config.paramConfig

import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import tororo1066.tororopluginapi.defaultMenus.LargeSInventory
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import java.lang.reflect.Field
import kotlin.reflect.KClass

@Suppress("unused")
interface IConfigParameters {

    private fun Field.setWithAccessible(obj: Any, value: Any) {
        val accessible = canAccess(obj)
        isAccessible = true
        set(obj, value)
        isAccessible = accessible
    }

    private fun getParameters(): List<Field> {
        return javaClass.declaredFields.filter { it.isAnnotationPresent(Parameter::class.java) }
    }

    private fun getTypeInstance(type: KClass<out AbstractParameterType<*>>): AbstractParameterType<*> {
        return type.java.getConstructor().newInstance()
    }

    fun loadParameters(configurationSection: ConfigurationSection) {
        getParameters().forEach { field ->
            val annotation = field.getAnnotation(Parameter::class.java) ?: return@forEach
            val path = annotation.key.ifEmpty { field.name }
            val value = configurationSection.get(path) ?: return@forEach
            field.setWithAccessible(this, getTypeInstance(annotation.type).getValue(value) ?: return@forEach)
        }
    }

    fun editGUI(configurationSection: ConfigurationSection): SInventory {
        return object : LargeSInventory("Edit") {
            override fun renderMenu(): Boolean {
                val items = ArrayList<SInventoryItem>()
                getParameters().forEach { field ->
                    val annotation = field.getAnnotation(Parameter::class.java) ?: return@forEach
                    val type = getTypeInstance(annotation.type)
                    items.add(getItem(field, annotation, type))
                }
                setResourceItems(items)
                return true
            }
        }
    }

    fun setParameters(configurationSection: ConfigurationSection) {
        getParameters().forEach { field ->
            val annotation = field.getAnnotation(Parameter::class.java) ?: return@forEach
            val path = annotation.key.ifEmpty { field.name }
            val type = getTypeInstance(annotation.type)
            configurationSection.setValue(path, field, type)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> ConfigurationSection.setValue(path: String, field: Field, type: AbstractParameterType<T>) {
        val value = field.get(this@IConfigParameters)
        if (value is List<*>) {
            set(path, value.map { type.getConfigValue(it as T) })
        } else {
            set(path, type.getConfigValue(value as T))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> SInventory.getItem(field: Field, annotation: Parameter, type: AbstractParameterType<T>): SInventoryItem {
        val path = annotation.key.ifEmpty { field.name }
        val value = field.get(this@IConfigParameters)
        return SInventoryItem(annotation.display)
            .setDisplayName(annotation.name)
            .addLore(annotation.description)
            .addLore("§7Current: §f${if (value as? T != null) type.getStringInfo(value) else "List"}")
            .setCanClick(false)
            .setClickEvent {
                if (field.type.isAssignableFrom(List::class.java)) {
                    listGUI(field, annotation, type as AbstractParameterType<Any>).open(it.whoClicked as Player)
                } else {
                    type.sendUpdate(it.whoClicked as Player, this, this@IConfigParameters, field, path, value as T)
                }
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> listGUI(field: Field, annotation: Parameter, type: AbstractParameterType<T>): SInventory {
        val path = annotation.key.ifEmpty { field.name }
        return object : LargeSInventory("Edit") {
            override fun renderMenu(): Boolean {
                val items = ArrayList<SInventoryItem>()
                val list = field.get(this@IConfigParameters) as List<T>
                list.forEach { value ->
                    items.add(SInventoryItem(annotation.display)
                        .setDisplayName(annotation.name)
                        .addLore(annotation.description)
                        .addLore(
                            "§7Current: §f${type.getStringInfo(value)}",
                            "§aClick to edit",
                            "§cShift + Click to remove"
                        )
                        .setClickEvent {
                            if (it.isShiftClick) {
                                field.setWithAccessible(this@IConfigParameters, list - value)
                                allRenderMenu()
                            } else {
                                type.sendUpdate(
                                    it.whoClicked as Player,
                                    this,
                                    this@IConfigParameters,
                                    field,
                                    path,
                                    value
                                )
                            }
                        })
                }

                items.add(SInventoryItem(Material.EMERALD_BLOCK)
                    .setDisplayName("§aAdd")
                    .setClickEvent {
                        type.sendUpdate(it.whoClicked as Player, this, this@IConfigParameters, field, path, null)
                    })
                setResourceItems(items)
                return true
            }
        }
    }
}