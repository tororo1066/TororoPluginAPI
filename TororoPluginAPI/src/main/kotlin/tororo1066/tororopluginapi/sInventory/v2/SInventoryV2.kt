package tororo1066.tororopluginapi.sInventory.v2

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.sEvent.SEvent
import java.util.concurrent.ConcurrentHashMap

abstract class SInventoryV2 {

    companion object {
        val sEvent = SEvent()


    }

    val items = ConcurrentHashMap<Int, ModernItemStack>()

    fun set(index: Int, material: Material, scope: ModernItemStack.() -> Unit) {
        val item = ModernItemStack(ItemStack(material))
        item.scope()
        items[index] = item
    }

    abstract fun render()
}

class TestSInventoryV2: SInventoryV2() {
//    init {
//        set(0, Material.DIAMOND) {
//            displayNameText = "§bTest Item"
//            loreText {
//                +"This is a test item"
//                +"Created using SInventoryV2"
//            }
//            customClickAction()
//        }
//    }

    override fun render() {
        set(0, Material.DIAMOND) {
            displayNameText = "§bTest Item"
            loreText {
                +"This is a test item"
                +"Created using SInventoryV2"
            }
            customClickAction()
        }
    }

    fun ModernItemStack.customClickAction() {
        cancelClickEvent()
        onClick {
            player.sendMessage("You clicked on $displayNameText")
        }
    }
}