package tororo1066.tororopluginapi.sInventory

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SInput
import tororo1066.tororopluginapi.sEvent.SEvent
import tororo1066.tororopluginapi.sItem.SItem
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.BiConsumer
import java.util.function.Consumer
import kotlin.collections.HashMap

/**
 * 拡張機能を持たせたInventory
 *
 * inv共有厳禁！
 */
@Suppress("DEPRECATION")
abstract class SInventory(val plugin: JavaPlugin) {

    companion object{
        val inputNow = ArrayList<UUID>()
    }

    var name = "Inventory"
    var row = 9
    var inv : Inventory

    protected val thread: ExecutorService = Executors.newCachedThreadPool()

    protected val sEvent = SEvent(plugin)

    protected val onClose = ArrayList<Consumer<InventoryCloseEvent>>()
    protected val asyncOnClose = ArrayList<Consumer<InventoryCloseEvent>>()
    protected val onOpen = ArrayList<Consumer<Player>>()
    protected val asyncOnOpen = ArrayList<Consumer<Player>>()
    protected val onClick = ArrayList<Consumer<InventoryClickEvent>>()
    protected val asyncOnClick = ArrayList<Consumer<InventoryClickEvent>>()
    protected val items = HashMap<Int,SInventoryItem>()

    protected val openingPlayer = ArrayList<UUID>()

    protected var parent : Consumer<InventoryCloseEvent>? = null

    var throughEvent = ArrayList<UUID>()

    protected open var savePlaceItems = false

    constructor(plugin : JavaPlugin, name : String, row : Int) : this(plugin) {
        this.name = name
        if (row !in 1..6){
            throw IndexOutOfBoundsException("SInventoryのrowは1~6で指定してください")
        }
        this.row = row*9
        this.inv = Bukkit.createInventory(null,this.row,this.name)
    }

    init {
        this.inv = Bukkit.createInventory(null,row,name)
    }

    fun getSInvItems(): HashMap<Int, SInventoryItem> {
        return items
    }

    fun registerClickSound(){
        setOnClick {
            val p = it.whoClicked as Player
            p.playSound(p.location,Sound.UI_BUTTON_CLICK,1f,1f)
        }
    }

    fun savePlaceItems(boolean: Boolean){
        savePlaceItems = boolean
    }

    fun setParent(inventory: SInventory){
        parent = Consumer { inventory.open(it.player as Player) }
    }

    fun moveInventory(inventory: SInventory, p: Player){
        throughClose(p)
        inventory.open(p)
    }

    fun moveChildInventory(inventory: SInventory, p: Player){
        inventory.setParent(this)
        throughEvent.add(p.uniqueId)
        inventory.open(p)
    }

    fun throughClose(p: Player){
        throughEvent.add(p.uniqueId)
        p.closeInventory()
    }

    fun getItem(slot: Int): ItemStack? {
        return inv.getItem(slot)
    }

    fun getItems(slot: IntRange): List<ItemStack?> {
        return slot.toList().map { getItem(it) }
    }

    /**
     * アイテムをインベントリにセットする
     * @param slot 位置(0~row*9)
     * @param item SInventoryItem
     */
    fun setItem(slot : Int, item : SInventoryItem){
        items[slot] = item
        inv.setItem(slot,item)
    }

    /**
     * アイテムをインベントリにセットする
     * @param slot 位置(0~row*9)
     * @param item SItem
     */
    fun setItem(slot : Int, item : SItem){
        setItem(slot, SInventoryItem(item))
    }

    /**
     * アイテムをインベントリにセットする
     * @param slot 位置(0~row*9)
     * @param item ItemStack
     */
    fun setItem(slot : Int, item : ItemStack){
        setItem(slot, SInventoryItem(item))
    }

    /**
     * アイテムをインベントリにセットする
     * @param slot 位置(0~row*9)
     * @param material Material
     */
    fun setItem(slot : Int, material : Material){
        setItem(slot, SInventoryItem(material))
    }

    /**
     * 指定したスロットにアイテムをセットする
     * @param slot 位置(List)
     * @param item SInventoryItem
     */
    fun setItems(slot : List<Int>, item: SInventoryItem){
        for (i in slot){
            setItem(i, item)
        }
    }

    /**
     * 指定したスロットにアイテムをセットする
     * @param slot 位置(List)
     * @param item SItem
     */
    fun setItems(slot : List<Int>, item: SItem){
        setItems(slot, SInventoryItem(item))
    }

    /**
     * 指定したスロットにアイテムをセットする
     * @param slot 位置(List)
     * @param item ItemStack
     */
    fun setItems(slot : List<Int>, item: ItemStack){
        setItems(slot, SInventoryItem(item))
    }

    /**
     * 指定したスロットにアイテムをセットする
     * @param slot 位置(List)
     * @param material Material
     */
    fun setItems(slot : List<Int>, material: Material){
        setItems(slot, SInventoryItem(material))
    }

    fun setItems(slot : IntRange, item: SInventoryItem){
        setItems(slot.toList(),item)
    }

    fun setItems(slot : IntRange, item: SItem){
        setItems(slot.toList(),item)
    }

    fun setItems(slot : IntRange, item: ItemStack){
        setItems(slot.toList(),item)
    }

    fun setItems(slot : IntRange, material: Material){
        setItems(slot.toList(),material)
    }

    /**
     * インベントリをクリアする
     */
    fun clear(){
        items.clear()
        inv.clear()
    }


    /**
     * 指定したスロットのアイテムを削除する
     * @param slot 位置
     */
    fun removeItem(slot : Int){
        items.remove(slot)
        inv.clear(slot)
    }

    fun removeItems(slot : List<Int>){
        slot.forEach {
            removeItem(it)
        }
    }

    fun removeItems(slot : IntRange){
        removeItems(slot.toList())
    }

    /**
     * アイテムを敷き詰める
     * @param item SInventoryItem
     */
    fun fillItem(item: SInventoryItem){
        setItems(0 until row,item)
    }


    /**
     * アイテムを敷き詰める
     * @param item SItem
     */
    fun fillItem(item: SItem){
        fillItem(SInventoryItem(item))
    }

    /**
     * アイテムを敷き詰める
     * @param item ItemStack
     */
    fun fillItem(item: ItemStack){
        fillItem(SInventoryItem(item))
    }

    /**
     * アイテムを敷き詰める
     * @param material Material
     */
    fun fillItem(material: Material){
        fillItem(SInventoryItem(material))
    }


    /**
     * インベントリを閉じたときに行う処理
     * @param event 処理
     */
    fun setOnClose(event : Consumer<InventoryCloseEvent>){
        onClose.add(event)
    }

    /**
     * インベントリを閉じたときに非同期で行う処理
     * @param event 処理
     */
    fun setAsyncOnClose(event : Consumer<InventoryCloseEvent>){
        asyncOnClose.add(event)
    }

    /**
     * インベントリを開いたときに行う処理
     * @param event 処理
     */
    fun setOnOpen(event : Consumer<Player>){
        onOpen.add(event)
    }

    /**
     * インベントリを開いたときに非同期で行う処理
     * @param event 処理
     */
    fun setAsyncOnOpen(event : Consumer<Player>){
        asyncOnOpen.add(event)
    }

    /**
     * インベントリをクリックしたときに行う処理
     * @param event 処理
     */
    fun setOnClick(event : Consumer<InventoryClickEvent>){
        onClick.add(event)
    }

    /**
     * インベントリをクリックしたときに非同期で行う処理
     * @param event 処理
     */
    fun setAsyncOnClick(event : Consumer<InventoryClickEvent>){
        asyncOnClick.add(event)
    }



    /**
     * インベントリを開かせる
     * @param p Player
     */
    open fun open(p : Player){
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (inputNow.contains(p.uniqueId)){
                p.sendMessage("§4You are entering some information.")
                return@Runnable
            }
            val saveItems = HashMap<Int,ItemStack>()
            if (savePlaceItems){
                (0 until row).forEach {
                    if (items.containsKey(it))return@forEach
                    saveItems[it] = inv.getItem(it)?:return@forEach
                }
            }
            if (!renderMenu(p)) return@Runnable
            if (!renderMenu()) return@Runnable
            afterRenderMenu(p)
            afterRenderMenu()
            if (savePlaceItems){
                saveItems.forEach { (i, item) ->
                    inv.setItem(i,item)
                }
            }
            p.openInventory(inv)

            for (open in onOpen){
                open.accept(p)
            }
            for (open in asyncOnOpen){
                thread.execute { open.accept(p) }
            }

            sEvent.register(InventoryCloseEvent::class.java) {
                if (!openingPlayer.contains(it.player.uniqueId))return@register
                openingPlayer.remove(it.player.uniqueId)
                sEvent.unregisterAll()
                if (throughEvent.remove(it.player.uniqueId)){
                    return@register
                }
                for (close in onClose){
                    close.accept(it)
                }
                for (close in asyncOnClose){
                    thread.execute { close.accept(it) }
                }

                parent?.accept(it)
            }

            sEvent.register(InventoryClickEvent::class.java) {
                if (!openingPlayer.contains(it.whoClicked.uniqueId))return@register

                for (click in onClick){
                    click.accept(it)
                }
                for (click in asyncOnClick){
                    thread.execute { click.accept(it) }
                }

                items[it.rawSlot]?.active(it)

            }

            openingPlayer.add(p.uniqueId)

        })
    }

    /**
     * 読み込み時に行う処理
     * @return falseでそのあとの処理を実行しない
     */
    open fun renderMenu(p: Player) : Boolean{
        return true
    }

    /**
     * 読み込み時に行う処理
     * @return falseでそのあとの処理を実行しない
     */
    open fun renderMenu() : Boolean{
        return true
    }

    /**
     * 読み込み後に行う処理
     */
    open fun afterRenderMenu(p: Player){

    }

    /**
     * 読み込み後に行う処理
     */
    open fun afterRenderMenu(){

    }

    open fun allRenderMenu(p: Player){
        if (!renderMenu(p))return
        if (!renderMenu())return
        afterRenderMenu(p)
        afterRenderMenu()
    }

    open fun allRenderMenu(){
        if (!renderMenu())return
        afterRenderMenu()
    }

    private fun createInputItem0(
        item: SItem,
        type: Class<*>,
        message: String = "§a/<入れるデータ(§d${type.simpleName}§a)>",
        action: BiConsumer<String,Player>,
        clickType: List<ClickType> = listOf(),
        invOpenCancel: Boolean = false
    ): SInventoryItem {
        return SInventoryItem(item).setCanClick(false).setClickEvent {
            if (clickType.isNotEmpty() && !clickType.contains(it.click)) return@setClickEvent
            val p = it.whoClicked as Player
            p.sendMessage(message)
            throughClose(p)
            inputNow.add(p.uniqueId)
            SEvent(plugin).biRegister(PlayerCommandPreprocessEvent::class.java) { cEvent, unit ->
                if (cEvent.player.uniqueId != p.uniqueId) return@biRegister
                cEvent.isCancelled = true

                if (cEvent.message == "/cancel") {
                    cEvent.player.sendMessage("§a入力をキャンセルしました")
                    unit.unregister()
                    inputNow.remove(cEvent.player.uniqueId)
                    if (!invOpenCancel) open(cEvent.player)
                    return@biRegister
                }

                action.accept(cEvent.message.replaceFirst("/", ""), cEvent.player)

            }
        }
    }


    fun <T> createNullableInputItem(
        item: SItem,
        type: Class<T>,
        message: String = "§a/<入れるデータ(§d${type.simpleName}§a)>",
        errorMsg: (String) -> String = {"§d${it}§4は§d${type.simpleName}§4ではありません"},
        clickType: List<ClickType> = listOf(),
        invOpenCancel: Boolean = false,
        action: BiConsumer<T?,Player>,
    ): SInventoryItem {
        return createInputItem0(item, type, message, BiConsumer { msg, p ->
            val (blank, value) = SInput.modifyClassValue(type, msg, allowEmpty = true)
            if (!blank && value == null) {
                p.sendMessage(errorMsg.invoke(msg))
                return@BiConsumer
            }
            action.accept(value, p)
        }, clickType, invOpenCancel)
    }

    fun <T> createInputItem(
        item: SItem,
        type: Class<T>,
        message: String = "§a/<入れるデータ(§d${type.simpleName}§a)>",
        errorMsg: (String) -> String = {"§d${it}§4は§d${type.simpleName}§4ではありません"},
        clickType: List<ClickType> = listOf(),
        invOpenCancel: Boolean = false,
        action: BiConsumer<T,Player>
    ): SInventoryItem {
        return createInputItem0(item, type, message, BiConsumer { msg, p ->
            val (_, value) = SInput.modifyClassValue(type, msg)
            if (value == null) {
                p.sendMessage(errorMsg.invoke(msg))
                return@BiConsumer
            }
            action.accept(value, p)
        }, clickType, invOpenCancel)
    }


}