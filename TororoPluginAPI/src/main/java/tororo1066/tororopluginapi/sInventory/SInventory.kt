package tororo1066.tororopluginapi.sInventory

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
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

/**
 * 拡張機能を持たせたInventory
 */
abstract class SInventory(val plugin: JavaPlugin) {

    companion object{
        val inputNow = ArrayList<UUID>()
    }

    private var name = "Inventory"
    private var row = 9
    private var inv : Inventory

    private val thread: ExecutorService = Executors.newCachedThreadPool()

    private val sEvent = SEvent(plugin)

    private val onClose = ArrayList<Consumer<InventoryCloseEvent>>()
    private val asyncOnClose = ArrayList<Consumer<InventoryCloseEvent>>()
    private val onOpen = ArrayList<Consumer<Player>>()
    private val asyncOnOpen = ArrayList<Consumer<Player>>()
    private val onClick = ArrayList<Consumer<InventoryClickEvent>>()
    private val asyncOnClick = ArrayList<Consumer<InventoryClickEvent>>()
    private val items = HashMap<Int,SInventoryItem>()

    private val openingPlayer = ArrayList<UUID>()

    private var parent : Consumer<InventoryCloseEvent>? = null

    private var throughEvent = ArrayList<UUID>()

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

    fun setParent(inventory: SInventory){
        parent = Consumer { inventory.open(it.player as Player) }
    }

    fun moveInventory(inventory: SInventory, p: Player){
        throughClose(p)
        inventory.open(p)
    }

    fun moveChildInventory(inventory: SInventory, p: Player){
        throughClose(p)
        inventory.setParent(this)
        inventory.open(p)
    }

    fun throughClose(p: Player){
        throughEvent.add(p.uniqueId)
        p.closeInventory()
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

    /**
     * アイテムを敷き詰める
     * @param item SInventoryItem
     */
    fun fillItem(item: SInventoryItem){
        for (i in 0..row*9){
            items[i] = item
            inv.setItem(i,item)
        }
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
    fun open(p : Player){
        plugin.server.scheduler.runTask(plugin, Runnable {
            if (inputNow.contains(p.uniqueId)){
                p.sendMessage("§4何かしらの情報を入力中です")
                return@Runnable
            }
            if (!renderMenu()) return@Runnable
            afterRenderMenu()

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


            for (open in onOpen){
                open.accept(p)
            }
            for (open in asyncOnOpen){
                thread.execute { open.accept(p) }
            }

            openingPlayer.add(p.uniqueId)
            p.openInventory(inv)
        })
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
    open fun afterRenderMenu(){

    }

    fun <T>createInputItem(item: SItem, type: Class<T>, message: String, action: BiConsumer<T,Player>, errorMsg: (String) -> String, clickType: List<ClickType>, invOpenCancel: Boolean): SInventoryItem {
        return SInventoryItem(item).setCanClick(false).setClickEvent {
            if (clickType.isNotEmpty() && !clickType.contains(it.click))return@setClickEvent
            val p = it.whoClicked as Player
            p.sendMessage(message)
            throughClose(p)
            inputNow.add(p.uniqueId)
            SEvent(plugin).biRegister(PlayerCommandPreprocessEvent::class.java) { cEvent, unit ->
                if (cEvent.player != p)return@biRegister
                cEvent.isCancelled = true

                if (cEvent.message == "/cancel"){
                    p.sendMessage("§a入力をキャンセルしました")
                    unit.unregister()
                    inputNow.remove(p.uniqueId)
                    if (!invOpenCancel) open(p)
                    return@biRegister
                }
                val msg = cEvent.message.replaceFirst("/","")
                val modifyValue = SInput.modifyClassValue(type,msg)
                if (modifyValue == null){
                    p.sendMessage(errorMsg.invoke(msg))
                    return@biRegister
                }

                action.accept(modifyValue,p)

                unit.unregister()
                inputNow.remove(p.uniqueId)
                if (!invOpenCancel) open(p)
            }
        }
    }

    fun <T>createInputItem(item: SItem, type: Class<T>, message: String, clickType: List<ClickType>, action: BiConsumer<T,Player>): SInventoryItem {
        return createInputItem(item, type, message, action,{"§d${it}§4は§d${type.simpleName}§4ではありません"},clickType, false)
    }

    fun <T>createInputItem(item: SItem, type: Class<T>, message: String, clickType: List<ClickType>, invOpenCancel: Boolean, action: BiConsumer<T,Player>): SInventoryItem {
        return createInputItem(item, type, message, action,{"§d${it}§4は§d${type.simpleName}§4ではありません"},clickType, invOpenCancel)
    }

    fun <T>createInputItem(item: SItem, type: Class<T>, message: String, clickType: ClickType, action: BiConsumer<T,Player>): SInventoryItem {
        return createInputItem(item, type, message, action,{"§d${it}§4は§d${type.simpleName}§4ではありません"}, listOf(clickType), false)
    }

    fun <T>createInputItem(item: SItem, type: Class<T>, message: String, clickType: ClickType, invOpenCancel: Boolean, action: BiConsumer<T,Player>): SInventoryItem {
        return createInputItem(item, type, message, action,{"§d${it}§4は§d${type.simpleName}§4ではありません"}, listOf(clickType), invOpenCancel)
    }

    fun <T>createInputItem(item: SItem, type: Class<T>, message: String, action: BiConsumer<T,Player>): SInventoryItem {
        return createInputItem(item, type, message, action,{"§d${it}§4は§d${type.simpleName}§4ではありません"}, listOf(), false)
    }

    fun <T>createInputItem(item: SItem, type: Class<T>, message: String, invOpenCancel: Boolean, action: BiConsumer<T,Player>): SInventoryItem {
        return createInputItem(item, type, message, action,{"§d${it}§4は§d${type.simpleName}§4ではありません"}, listOf(), invOpenCancel)
    }

    fun <T>createInputItem(item: SItem, type: Class<T>, clickType: List<ClickType>, action: BiConsumer<T,Player>): SInventoryItem {
        return createInputItem(item, type, "§a/<入れるデータ(§d${type.simpleName}§a)>", clickType, action)
    }

    fun <T>createInputItem(item: SItem, type: Class<T>, clickType: List<ClickType>, invOpenCancel: Boolean, action: BiConsumer<T,Player>): SInventoryItem {
        return createInputItem(item, type, "§a/<入れるデータ(§d${type.simpleName}§a)>", clickType, invOpenCancel, action)
    }

    fun <T>createInputItem(item: SItem, type: Class<T>, clickType: ClickType, action: BiConsumer<T,Player>): SInventoryItem {
        return createInputItem(item, type, "§a/<入れるデータ(§d${type.simpleName}§a)>", listOf(clickType), action)
    }

    fun <T>createInputItem(item: SItem, type: Class<T>, clickType: ClickType, invOpenCancel: Boolean, action: BiConsumer<T,Player>): SInventoryItem {
        return createInputItem(item, type, "§a/<入れるデータ(§d${type.simpleName}§a)>", listOf(clickType), invOpenCancel, action)
    }

    fun <T>createInputItem(item: SItem, type: Class<T>, action: BiConsumer<T,Player>): SInventoryItem {
        return createInputItem(item, type, "§a/<入れるデータ(§d${type.simpleName}§a)>", listOf(), action)
    }

    fun <T>createInputItem(item: SItem, type: Class<T>, invOpenCancel: Boolean, action: BiConsumer<T,Player>): SInventoryItem {
        return createInputItem(item, type, "§a/<入れるデータ(§d${type.simpleName}§a)>", listOf(), invOpenCancel, action)
    }



}