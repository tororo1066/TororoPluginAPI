package tororo1066.tororopluginapi.sInventory

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.entity.SPlayer
import tororo1066.tororopluginapi.sEvent.SEvent
import tororo1066.tororopluginapi.sEvent.SEventUnit
import tororo1066.tororopluginapi.frombukkit.SBukkit
import tororo1066.tororopluginapi.sItem.SItem
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Consumer
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

abstract class SInventory(val plugin: JavaPlugin) {

    private var name = "Inventory"
    private var row = 9
    private var inv : Inventory

    private val thread: ExecutorService = Executors.newCachedThreadPool()

    private val onClose = ArrayList<Consumer<InventoryCloseEvent>>()
    private val asyncOnClose = ArrayList<Consumer<InventoryCloseEvent>>()
    private val onOpen = ArrayList<Consumer<SPlayer>>()
    private val asyncOnOpen = ArrayList<Consumer<SPlayer>>()
    private val onClick = ArrayList<Consumer<InventoryClickEvent>>()
    private val asyncOnClick = ArrayList<Consumer<InventoryClickEvent>>()
    private val items = HashMap<Int,SInventoryItem>()

    private val events = ArrayList<SEventUnit<*>>()

    private val openingPlayer = ArrayList<UUID>()

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

    fun setInvName(){

    }

    fun setItem(slot : Int, item : SInventoryItem){
        items[slot] = item
        inv.setItem(slot,item)
    }

    fun setItem(slot : Int, item : ItemStack){
        setItem(slot, SInventoryItem(item))
    }

    fun setItem(slot : Int, material : Material){
        setItem(slot, SInventoryItem(material))
    }

    fun setItems(slot : List<Int>, item: SInventoryItem){
        for (i in slot){
            setItem(i, item)
        }
    }

    fun setItems(slot : List<Int>, item: SItem){
        setItems(slot, SInventoryItem(item))
    }

    fun setItems(slot : List<Int>, item: ItemStack){
        setItems(slot, SInventoryItem(item))
    }

    fun setItems(slot : List<Int>, material: Material){
        setItems(slot, SInventoryItem(material))
    }

    fun clear(){
        items.clear()
        inv.clear()
    }





    fun removeItem(slot : Int){
        items.remove(slot)
        inv.setItem(slot,null)
    }


    fun fillItem(item: SInventoryItem){
        for (i in 0..row*9){
            items[i] = item
            inv.setItem(i,item)
        }
    }

    fun fillItem(item: SItem){
        fillItem(SInventoryItem(item))
    }

    fun fillItem(item: ItemStack){
        fillItem(SInventoryItem(item))
    }

    fun fillItem(material: Material){
        fillItem(SInventoryItem(material))
    }



    fun setOnClose(event : Consumer<InventoryCloseEvent>){
        onClose.add(event)
    }

    fun setAsyncOnClose(event : Consumer<InventoryCloseEvent>){
        asyncOnClose.add(event)
    }

    fun setOnOpen(event : Consumer<SPlayer>){
        onOpen.add(event)
    }

    fun setAsyncOnOpen(event : Consumer<SPlayer>){
        asyncOnOpen.add(event)
    }

    fun setOnClick(event : Consumer<InventoryClickEvent>){
        onClick.add(event)
    }

    fun setAsyncOnClick(event : Consumer<InventoryClickEvent>){
        asyncOnClick.add(event)
    }

    fun open(p : Player){
        open(SBukkit.getSPlayer(p))
    }



    fun open(p : SPlayer){
        plugin.server.scheduler.runTask(plugin, Runnable {
            if (!renderMenu()) return@Runnable
            afterRenderMenu()
            for (open in onOpen){
                open.accept(p)
            }
            for (open in asyncOnOpen){
                thread.execute { open.accept(p) }
            }
            events.add(SEvent(plugin).register(InventoryCloseEvent::class.java) {
                if (!openingPlayer.contains(it.player.uniqueId))return@register
                openingPlayer.remove(it.player.uniqueId)
                for (close in onClose){
                    close.accept(it)
                }
                for (close in asyncOnClose){
                    thread.execute { close.accept(it) }
                }

                events.forEach { it2 ->
                    it2.unregister()
                }

            })

            events.add(SEvent(plugin).register(InventoryClickEvent::class.java) {
                if (!openingPlayer.contains(it.whoClicked.uniqueId))return@register
                for (click in onClick){
                    click.accept(it)
                }
                for (click in asyncOnClick){
                    thread.execute { click.accept(it) }
                }

                if (items.containsKey(it.rawSlot)){
                    items[it.rawSlot]!!.active(it)
                }
            })

            openingPlayer.add(p.uniqueId)
            p.openInventory(inv)
        })
    }

    open fun renderMenu() : Boolean{
        return true
    }

    open fun afterRenderMenu(){

    }






}