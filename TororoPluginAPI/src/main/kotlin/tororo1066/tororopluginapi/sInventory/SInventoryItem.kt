package tororo1066.tororopluginapi.sInventory

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.banner.Pattern
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.sItem.SItem
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * SInventoryに使えるitem。eventとか入れられる
 */
open class SInventoryItem(itemStack: ItemStack) : SItem(itemStack) {

    private val clickEvent = ArrayList<Consumer<InventoryClickEvent>>()
    private val biClickEvent = ArrayList<BiConsumer<SInventoryItem,InventoryClickEvent>>()
    private val asyncClickEvent = ArrayList<Consumer<InventoryClickEvent>>()
    private val biAsyncClickEvent = ArrayList<BiConsumer<SInventoryItem,InventoryClickEvent>>()
    private var canClick = true
    private val thread: ExecutorService = Executors.newCachedThreadPool()

    constructor(material: Material) : this(ItemStack(material))

    constructor(sItem: SItem) : this(sItem.build())

    init {
        setClickEvent { if (!canClick) it.isCancelled = true }
    }

    /**
     * このアイテムをクリックしたときに行う処理
     * @param event 処理
     */
    fun setClickEvent(event : Consumer<InventoryClickEvent>): SInventoryItem {
        clickEvent.add(event)
        return this
    }

    fun setBiClickEvent(event: BiConsumer<SInventoryItem,InventoryClickEvent>): SInventoryItem {
        biClickEvent.add(event)
        return this
    }

    fun setAsyncBiClickEvent(event: BiConsumer<SInventoryItem,InventoryClickEvent>): SInventoryItem {
        biAsyncClickEvent.add(event)
        return this
    }

    /**
     * このアイテムをクリックしたときに非同期で行う処理
     * @param event 処理
     */
    fun setAsyncClickEvent(event : Consumer<InventoryClickEvent>): SInventoryItem {
        asyncClickEvent.add(event)
        return this
    }

    /**
     * クリック可能かを設定する
     * @param boolean trueならキャンセルする、falseならしない
     */
    fun setCanClick(boolean: Boolean): SInventoryItem {
        canClick = boolean
        return this
    }

    fun sound(sound: Sound, volume: Float, pitch: Float): SInventoryItem {
        return setClickEvent {
            val p = it.whoClicked as Player
            p.playSound(p.location,sound,volume,pitch)
        }
    }

    fun uiSound(): SInventoryItem {
        return sound(Sound.UI_BUTTON_CLICK,1f,1f)
    }

    fun active(e : InventoryClickEvent){
        for (event in clickEvent){
            event.accept(e)
        }

        for (event in biClickEvent){
            event.accept(this,e)
        }

        for (event in asyncClickEvent){
            thread.execute { event.accept(e) }
        }

        for (event in biAsyncClickEvent){
            thread.execute { event.accept(this,e) }
        }
    }

    override fun setItemAmount(amount: Int): SInventoryItem {
        return super.setItemAmount(amount) as SInventoryItem
    }

    override fun setDisplayName(name: String): SInventoryItem {
        return super.setDisplayName(name) as SInventoryItem
    }

    override fun setSStrDisplayName(sStr: SStr): SInventoryItem {
        return super.setSStrDisplayName(sStr) as SInventoryItem
    }

    override fun setLore(lore: List<String>): SInventoryItem {
        return super.setLore(lore) as SInventoryItem
    }

    override fun setLore(vararg lore: String): SInventoryItem {
        return super.setLore(*lore) as SInventoryItem
    }

    override fun setSStrLore(sStr: List<SStr>): SInventoryItem {
        return super.setSStrLore(sStr) as SInventoryItem
    }

    override fun addLore(lore: List<String>): SInventoryItem {
        return super.addLore(lore) as SInventoryItem
    }

    override fun addLore(vararg lore : String): SInventoryItem {
        return super.addLore(*lore) as SInventoryItem
    }

    override fun addSStrLore(sStr: List<SStr>): SInventoryItem {
        return super.addSStrLore(sStr) as SInventoryItem
    }

    override fun addSStrLore(vararg sStr: SStr): SInventoryItem {
        return super.addSStrLore(*sStr) as SInventoryItem
    }

    override fun setCustomModelData(cmd: Int): SInventoryItem {
        return super.setCustomModelData(cmd) as SInventoryItem
    }

    override fun <T : Any, Z : Any> setCustomData(
        plugin: JavaPlugin,
        key: String,
        type: PersistentDataType<T, Z>,
        value: Z
    ): SInventoryItem {
        return super.setCustomData(plugin, key, type, value) as SInventoryItem
    }

    override fun setEnchantment(enchantment: Enchantment, level: Int): SInventoryItem {
        return super.setEnchantment(enchantment, level) as SInventoryItem
    }

    override fun setSkullOwner(uuid: UUID): SInventoryItem {
        return super.setSkullOwner(uuid) as SInventoryItem
    }

    override fun addPattern(pattern: Pattern): SInventoryItem {
        return super.addPattern(pattern) as SInventoryItem
    }

    override fun addItemFlags(vararg flags: ItemFlag): SInventoryItem {
        return super.addItemFlags(*flags) as SInventoryItem
    }

    override fun removeItemFlags(vararg flags: ItemFlag): SInventoryItem {
        return super.removeItemFlags(*flags) as SInventoryItem
    }

    override fun clone(): SInventoryItem {
        return super.clone() as SInventoryItem
    }

}