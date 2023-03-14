package tororo1066.tororopluginapi.sInventory

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.block.banner.Pattern
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BannerMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.sItem.SItem
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.BiConsumer
import java.util.function.Consumer
import kotlin.collections.ArrayList

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

    constructor(sItem: SItem) : this(sItem as ItemStack)

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
        this.amount = amount
        return this
    }

    override fun setDisplayName(name: String): SInventoryItem {
        val meta = itemMeta?:return this
        meta.setDisplayName(name)
        itemMeta = meta
        return this
    }

    override fun setLore(lore: List<String>): SInventoryItem {
        val meta = itemMeta?:return this
        meta.lore = lore
        itemMeta = meta
        return this
    }

    override fun addLore(lore: List<String>): SInventoryItem {
        return setLore(getStringLore().toMutableList().apply { addAll(lore) })
    }

    override fun addLore(lore: String): SInventoryItem {
        return addLore(mutableListOf(lore))
    }

    override fun addLore(vararg lore : String): SInventoryItem {
        return addLore(lore.toList())
    }

    override fun setCustomModelData(csm: Int): SInventoryItem {
        val meta = itemMeta?:return this
        meta.setCustomModelData(csm)
        itemMeta = meta
        return this
    }

    override fun <T : Any> setCustomData(plugin: JavaPlugin, key: String, type: PersistentDataType<T, T>, value: T): SInventoryItem {
        val meta = this.itemMeta?:return this
        meta.persistentDataContainer[NamespacedKey(plugin,key),type] = value
        this.itemMeta = meta
        return this
    }

    override fun setEnchantment(enchantment: Enchantment, level: Int): SInventoryItem {
        this.addUnsafeEnchantment(enchantment,level)
        return this
    }

    override fun setSkullOwner(uuid: UUID): SInventoryItem {
        val meta = itemMeta as SkullMeta
        meta.owningPlayer = Bukkit.getOfflinePlayer(uuid)
        itemMeta = meta
        return this
    }

    override fun addPattern(pattern: Pattern): SItem {
        val meta = itemMeta as BannerMeta
        meta.addPattern(pattern)
        itemMeta = meta
        return this
    }



}