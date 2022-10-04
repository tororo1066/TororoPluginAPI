package tororo1066.tororopluginapi.lang

import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SInput
import tororo1066.tororopluginapi.defaultMenus.LargeSInventory
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem
import java.io.File

class LangEditor(plugin: JavaPlugin): LargeSInventory(plugin,"LangEditor") {

    override fun renderMenu(): Boolean {
        val langFolder = File(plugin.dataFolder.path + "/LangFolder/")
        if (!langFolder.exists()) return false
        val languages = HashMap<String,YamlConfiguration>()
        (langFolder.listFiles()?:return false).forEach {
            languages[it.nameWithoutExtension] = YamlConfiguration.loadConfiguration(it)
        }
        val langItems = ArrayList<SInventoryItem>()
        languages.forEach { (str, yaml) ->
            langItems.add(SInventoryItem(Material.WRITABLE_BOOK).setDisplayName(str).setCanClick(false).setClickEvent { e ->
                val childInv = object : LargeSInventory(plugin,str) {
                    override fun renderMenu(): Boolean {
                        val items = ArrayList<SInventoryItem>()
                        yaml.getKeys(false).forEach {
                            val section = yaml.getConfigurationSection(it)
                            if (section == null){
                                items.add(createMsgEditItem(this,yaml,it,yaml,str))
                            } else {
                                items.add(createGotoNextMenuItem(this,yaml,it,yaml,str))
                            }
                        }
                        items.add(this.createInputItem(SItem(Material.EMERALD_BLOCK).setDisplayName("§aNew Insert"),String::class.java,"Please enter path.",true){ include, p ->
                            SInput(plugin).sendInputCUI(p,String::class.java,"Please enter translated message.") { message ->
                                yaml.set(include,message)
                                yaml.save(File(plugin.dataFolder.path + "/LangFolder/${str}.yml"))
                                this.open(p)
                            }
                        })
                        setResourceItems(items)
                        return true
                    }
                }

                moveChildInventory(childInv, e.whoClicked as Player)
            })
        }

        setResourceItems(langItems)
        return true
    }

    fun createMsgEditItem(inv: SInventory, section: ConfigurationSection, path: String, yaml: YamlConfiguration, fileName: String): SInventoryItem {
        return inv.createInputItem(SItem(Material.WRITTEN_BOOK).setDisplayName(path + " : " + section.getString(path)!!),String::class.java,"メッセージを入力してください") { str, p ->
            section.set(path,if (str == "null") null else str)
            yaml.save(File(plugin.dataFolder.path + "/LangFolder/${fileName}.yml"))
            SLang.langFile[fileName] = yaml
        }
    }

    fun createGotoNextMenuItem(inv: SInventory, section: ConfigurationSection, path: String, yaml: YamlConfiguration, fileName: String): SInventoryItem {
        return SInventoryItem(Material.WRITABLE_BOOK).setDisplayName(path).setCanClick(false).setClickEvent { e ->
            val newInv = object : LargeSInventory(plugin,path){
                override fun renderMenu(): Boolean {
                    val newSection = section.getConfigurationSection(path)!!
                    val items = ArrayList<SInventoryItem>()
                    newSection.getKeys(false).forEach {
                        val checkSection = newSection.getConfigurationSection(it)
                        if (checkSection == null){
                            items.add(createMsgEditItem(this,newSection,it,yaml,fileName))
                        } else {
                            items.add(createGotoNextMenuItem(this,newSection,it,yaml,fileName))
                        }
                    }

                    items.add(this.createInputItem(SItem(Material.EMERALD_BLOCK).setDisplayName("§aNew Insert"),String::class.java,"Please enter path.",true){ str, p ->
                        SInput(plugin).sendInputCUI(p,String::class.java,"Please enter translated message.") {
                            section.set("$path.$str",it)
                            yaml.save(File(plugin.dataFolder.path + "/LangFolder/${fileName}.yml"))
                            SLang.langFile[fileName] = yaml
                            this.open(p)
                        }
                    })

                    setResourceItems(items)
                    return true
                }
            }

            inv.moveChildInventory(newInv,e.whoClicked as Player)
        }
    }
}