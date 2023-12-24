package tororo1066.tororopluginapi.config

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.defaultMenus.LargeSInventory
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem
import tororo1066.tororopluginapi.utils.LocType
import tororo1066.tororopluginapi.utils.sendMessage
import tororo1066.tororopluginapi.utils.toLocString
import java.io.File

class ConfigEditor(plugin: JavaPlugin): LargeSInventory(plugin,"ConfigEditor") {

    override fun renderMenu(): Boolean {
        val pluginFolder = File(plugin.dataFolder.path + File.separator)
        if (!pluginFolder.exists()) return false
        val files = pluginFolder.listFiles()?:return false
        val resourceItems = ArrayList<SInventoryItem>()
        files.forEach { file ->
            if (file.isDirectory){
                resourceItems.add(gotoNextFolderItem(this,file))
            } else {
                if (file.extension == "yml"){
                    resourceItems.add(gotoYmlItem(this,file))
                }
            }
        }

        setResourceItems(resourceItems)
        return true
    }

    fun createEditItem(inv: SInventory, section: ConfigurationSection, path: String, yaml: YamlConfiguration, file: File): SInventoryItem {
        val value = section.get(path)!!
        val type = value.javaClass
        if (type.simpleName == "CraftItemStack"){
            val itemStack = value as ItemStack

            return SInventoryItem(Material.WRITTEN_BOOK).setDisplayName("§r${path}:${value.itemMeta.displayName}").addLore(itemInfoLore(itemStack)).addLore("§eType: ItemStack").setCanClick(false).setClickEvent {
                if (it.click == ClickType.SHIFT_RIGHT){
                    section.set(path,null)
                    yaml.save(file)
                    inv.renderMenu()
                    inv.afterRenderMenu()
                    return@setClickEvent
                }
                val p = it.whoClicked as Player
                if (p.inventory.itemInMainHand.type.isAir){
                    p.sendMessage("You must held item in main hand.")
                    return@setClickEvent
                }
                val item = p.inventory.itemInMainHand
                section.set(path,item)
                yaml.save(file)
                inv.allRenderMenu()
            }
        }

        if (type.simpleName == "SingletonList"){
            val list = (value as List<*>).filterNotNull().toMutableList()
            val listType = list.javaClass.componentType
            return SInventoryItem(Material.WRITTEN_BOOK).setDisplayName("§r${path}:List(${listType.simpleName})").setCanClick(false).setClickEvent {
                val newInv = object : LargeSInventory(plugin,"§aList §7(Type: ${listType.simpleName})"){
                    override fun renderMenu(): Boolean {
                        val items = ArrayList<SInventoryItem>()
                        list.forEach {

                        }
                        return true
                    }
                }
            }
        }
        val item = inv.createInputItem(SItem(Material.WRITTEN_BOOK).setDisplayName("§r${path}:${value}").addLore("§eType: ${type.simpleName}"),type,
            clickType =  listOf(ClickType.LEFT)){ variable, _ ->
            section.set(path,variable)
            yaml.save(file)
        }.setClickEvent {
            if (it.click == ClickType.SHIFT_RIGHT){
                section.set(path,null)
                yaml.save(file)
                inv.renderMenu()
                inv.afterRenderMenu()
                return@setClickEvent
            }
        }

        if (type == Location::class.java){
            return item.setDisplayName("§r${path}:${(value as Location).toLocString(LocType.ALL_SPACE)}")
        }

        return item

    }

    fun gotoNextFolderItem(inv: SInventory, folder: File): SInventoryItem {
        val item = SInventoryItem(Material.BOOKSHELF).setDisplayName(folder.nameWithoutExtension).setCanClick(false).setClickEvent {
            val files = folder.listFiles()
            if (files == null){
                it.whoClicked.sendMessage("This folder is empty.")
                return@setClickEvent
            }

            val newInv = object : LargeSInventory(plugin,folder.name){
                override fun renderMenu(): Boolean {
                    val items = ArrayList<SInventoryItem>()
                    files.forEach { file ->
                        if (file.isDirectory){
                            items.add(gotoNextFolderItem(this,file))
                        } else {
                            if (file.extension == "yml"){
                                items.add(gotoYmlItem(this,file))
                            }
                        }
                    }
                    setResourceItems(items)
                    return true
                }
            }
            inv.moveChildInventory(newInv,it.whoClicked as Player)
        }

        return item
    }

    fun gotoYmlItem(inv: SInventory, file: File): SInventoryItem {
        val item = SInventoryItem(Material.BOOK).setDisplayName(file.name).setCanClick(false).setClickEvent { e ->
            val yaml = YamlConfiguration.loadConfiguration(file)

            val newInv = object : LargeSInventory(plugin,file.name){
                override fun renderMenu(): Boolean {
                    val items = ArrayList<SInventoryItem>()
                    yaml.getKeys(false).forEach {
                        if (yaml.isConfigurationSection(it)){
                            items.add(createGotoNextMenuItem(this,yaml,it,yaml,file))
                        } else {
                            items.add(createEditItem(this,yaml,it,yaml,file))
                        }
                    }
                    items.add(newInsertItem(this,yaml,yaml,file))
                    setResourceItems(items)
                    return true
                }
            }
            inv.moveChildInventory(newInv,e.whoClicked as Player)
        }

        return item
    }

    fun createGotoNextMenuItem(inv: SInventory, section: ConfigurationSection, path: String, yaml: YamlConfiguration, file: File): SInventoryItem {
        return SInventoryItem(Material.WRITABLE_BOOK).setDisplayName(path).setCanClick(false).setClickEvent { e ->
            val newInv = object : LargeSInventory(plugin,path){
                override fun renderMenu(): Boolean {
                    val newSection = section.getConfigurationSection(path)!!
                    val items = ArrayList<SInventoryItem>()
                    newSection.getKeys(false).forEach {
                        if (newSection.isConfigurationSection(it)){
                            items.add(createEditItem(this,newSection,it,yaml,file))
                        } else {
                            items.add(createGotoNextMenuItem(this,newSection,it,yaml,file))
                        }
                    }

                    items.add(newInsertItem(this,newSection,yaml,file))

                    setResourceItems(items)
                    return true
                }
            }

            inv.moveChildInventory(newInv,e.whoClicked as Player)
        }
    }

    fun newInsertItem(inv: SInventory, section: ConfigurationSection, yaml: YamlConfiguration, file: File): SInventoryItem {

        fun saveAndBack(p: Player){
            yaml.save(file)
            inv.open(p)
        }

        val item = inv.createInputItem(SItem(Material.EMERALD_BLOCK).setDisplayName("§aAdd Value").addLore(listOf("§bTypes","§cString","§1Int","§bDouble","§eList","§6Location","§aItemStack")),String::class.java,"/<path>", invOpenCancel = true) { str, p ->
            if (str.matches(Regex("[(<|>:?\"/\\\\)*]"))){
                p.sendMessage("Don't use illegal character. Action cancelled.")
                return@createInputItem
            }


            fun generateInv(isList: Boolean): LargeSInventory {
                val newInv = object : LargeSInventory(plugin,"§aSelect Type §7(Path: ${str})"){
                    override fun renderMenu(): Boolean {
                        val items = arrayListOf(
                            createInputItem(SItem(Material.REDSTONE_BLOCK).setDisplayName("§cString"),String::class.java,"/<value(String)>") { value, _->
                                if (isList){
                                    section.set(str,listOf(value))
                                } else {
                                    section.set(str,value)
                                }
                                saveAndBack(p)
                            },
                            createInputItem(SItem(Material.LAPIS_BLOCK).setDisplayName("§1Int"),Int::class.java,"/<value(Int)>") { value, _->
                                if (isList){
                                    section.set(str,listOf(value))
                                } else {
                                    section.set(str,value)
                                }
                                saveAndBack(p)
                            },
                            createInputItem(SItem(Material.DIAMOND_BLOCK).setDisplayName("§bDouble"),Double::class.java,"/<value(Double)>") { value, _->
                                if (isList){
                                    section.set(str,listOf(value))
                                } else {
                                    section.set(str,value)
                                }
                                saveAndBack(p)
                            },
                            createInputItem(SItem(Material.GOLD_BLOCK).setDisplayName("§6Location"),Location::class.java,"/<world> <x> <y> <z> (yaw) (pitch)") { value, _->
                                if (isList){
                                    section.set(str,listOf(value))
                                } else {
                                    section.set(str,value)
                                }
                                saveAndBack(p)
                            },
                            createInputItem(SItem(Material.COAL_BLOCK).setDisplayName("§dBoolean"),Boolean::class.java,"/<value(Boolean)>") { value, _ ->
                                if (isList){
                                    section.set(str,listOf(value))
                                } else {
                                    section.set(str,value)
                                }
                            },
                            SInventoryItem(Material.EMERALD_BLOCK).setDisplayName("§aItemStack").setCanClick(false).setClickEvent {
                                if (p.inventory.itemInMainHand.type.isAir){
                                    p.sendMessage("You must held item in main hand.")
                                    return@setClickEvent
                                }
                                val item = p.inventory.itemInMainHand
                                if (isList){
                                    section.set(str,listOf(item))
                                } else {
                                    section.set(str,item)
                                }
                                saveAndBack(p)
                            }

                        )

                        if (!isList){
                            items.add(SInventoryItem(Material.IRON_BLOCK).setDisplayName("§eList").setCanClick(false).setClickEvent {
                                moveChildInventory(generateInv(true),it.whoClicked as Player)
                            })
                        }
                        setResourceItems(items)
                        return true
                    }
                }

                return newInv
            }

            inv.moveChildInventory(generateInv(false),p)
        }

        return item

    }

    private fun itemInfoLore(itemStack: ItemStack): ArrayList<String> {
        val lore = arrayListOf<String>()
        lore.add("§rType: ${itemStack.type.name}")
        lore.add("§rLore: ${if (itemStack.itemMeta.lore.isNullOrEmpty()) "Empty" else ""}")
        lore.addAll(itemStack.itemMeta.lore.orEmpty())
        val cmd = if (itemStack.itemMeta.hasCustomModelData()) itemStack.itemMeta.customModelData.toString() else "None"
        lore.add("§rCustomModelData: $cmd")
        lore.add("§risUnbreakable: ${itemStack.itemMeta.isUnbreakable}")
        if (itemStack.itemMeta is Damageable){
            lore.add("§rDurability: ${itemStack.type.maxDurability.toInt() - (itemStack.itemMeta as Damageable).damage}/${itemStack.type.maxDurability.toInt()}")
        }
        lore.add("§rEnchantments: ${if (itemStack.enchantments.isEmpty()) "Empty" else ""}")
        lore.addAll(itemStack.itemMeta.enchants.map { "§r${it.key.key.key} Level ${it.value}" })
        lore.add("§rItemFlags: ${if (itemStack.itemFlags.isEmpty()) "Empty" else ""}")
        lore.addAll(itemStack.itemFlags.map { "§r${it.name}" })
        lore.add("§rAttributes: ${if (itemStack.itemMeta.attributeModifiers == null || itemStack.itemMeta.attributeModifiers!!.isEmpty) "Empty" else ""}")
        itemStack.itemMeta.attributeModifiers?.forEach { data, level ->
            lore.add("§r${data.name} ${level.amount}")
        }
        return lore
    }
}