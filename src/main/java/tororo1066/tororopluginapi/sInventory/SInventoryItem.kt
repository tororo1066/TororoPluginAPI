package tororo1066.tororopluginapi.sInventory

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.sItem.SItem
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Consumer

/**
 * SInventoryに使えるitem。eventとか入れられる
 */
class SInventoryItem(itemStack: ItemStack) : SItem(itemStack) {

    private val clickEvent = ArrayList<Consumer<InventoryClickEvent>>()
    private val asyncClickEvent = ArrayList<Consumer<InventoryClickEvent>>()
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

    fun active(e : InventoryClickEvent){
        for (event in clickEvent){
            event.accept(e)
        }

        for (event in asyncClickEvent){
            thread.execute { event.accept(e) }
        }
    }

}