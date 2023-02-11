package tororo1066.tororopluginapi.defaultMenus

import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BannerMeta
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem
import java.util.function.Consumer

class NumericInputInventory(plugin: JavaPlugin, name: String): SInventory(plugin,name,6) {

    val numItems = LinkedHashMap<Int,ItemStack>()

    init {
        val zero = ItemStack(Material.WHITE_BANNER)
        zero.editMeta(BannerMeta::class.java) {
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_BOTTOM))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_RIGHT))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_TOP))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_LEFT))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_DOWNLEFT))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.BORDER))
            it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }

        val one = ItemStack(Material.WHITE_BANNER)
        one.editMeta(BannerMeta::class.java) {
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.SQUARE_TOP_LEFT))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_CENTER))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_BOTTOM))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.BORDER))
            it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }

        val two = ItemStack(Material.WHITE_BANNER)
        two.editMeta(BannerMeta::class.java) {
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_TOP))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.RHOMBUS_MIDDLE))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_DOWNLEFT))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_BOTTOM))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.BORDER))
            it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }

        val three = ItemStack(Material.WHITE_BANNER)
        three.editMeta(BannerMeta::class.java) {
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_MIDDLE))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.STRIPE_LEFT))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_BOTTOM))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_RIGHT))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_TOP))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.BORDER))
            it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }

        val four = ItemStack(Material.WHITE_BANNER)
        four.editMeta(BannerMeta::class.java) {
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.HALF_HORIZONTAL))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_LEFT))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.STRIPE_BOTTOM))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_RIGHT))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_MIDDLE))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.BORDER))
            it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }

        val five = ItemStack(Material.WHITE_BANNER)
        five.editMeta(BannerMeta::class.java) {
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_BOTTOM))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_DOWNRIGHT))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.CURLY_BORDER))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.SQUARE_BOTTOM_LEFT))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_TOP))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.BORDER))
            it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }

        val six = ItemStack(Material.BLACK_BANNER)
        six.editMeta(BannerMeta::class.java) {
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.HALF_HORIZONTAL_MIRROR))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_RIGHT))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.STRIPE_TOP))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_BOTTOM))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_LEFT))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.BORDER))
            it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }

        val seven = ItemStack(Material.WHITE_BANNER)
        seven.editMeta(BannerMeta::class.java) {
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_TOP))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.DIAGONAL_RIGHT))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_DOWNLEFT))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.SQUARE_BOTTOM_LEFT))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.BORDER))
            it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }

        val eight = ItemStack(Material.WHITE_BANNER)
        eight.editMeta(BannerMeta::class.java) {
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_TOP))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_LEFT))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_RIGHT))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_MIDDLE))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_BOTTOM))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.BORDER))
            it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }

        val nine = ItemStack(Material.BLACK_BANNER)
        nine.editMeta(BannerMeta::class.java) {
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.HALF_HORIZONTAL))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_LEFT))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.STRIPE_BOTTOM))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_TOP))
            it.addPattern(Pattern(DyeColor.BLACK,PatternType.STRIPE_RIGHT))
            it.addPattern(Pattern(DyeColor.WHITE,PatternType.BORDER))
            it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }

        numItems.putAll(mapOf(Pair(46,zero),Pair(37,one),Pair(38,two),Pair(39,three)
            ,Pair(28,four),Pair(29,five),Pair(30,six),Pair(19,seven),Pair(20,eight),Pair(21,nine)
        ))
    }

    var nowNum = 0L
    var maxDigits = 9
    var maxNum = -1L
    var allowZero = false
    var onConfirm: Consumer<Long>? = null
    var onCancel: Consumer<InventoryClickEvent>? = null
    var infoItem: ItemStack? = null

    private val numDisplay = arrayListOf(8,7,6,5,4,3,2,1,0,17,16,15,14,13,12,11,10,9)

    fun displayNumber(){
        removeItems(0 until maxDigits)

        for (i in 0 until nowNum.toString().length){
            val char = nowNum.toString()[i].toString()
            val item = SInventoryItem(numItems.values.toList()[char.toInt()]).setDisplayName(char)
                .setCanClick(false)
            setItem(numDisplay[nowNum.toString().length-1-i],item)
        }

        renderButton()
    }

    override fun renderMenu(): Boolean {
        fillItem(SInventoryItem(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").setCanClick(false))

        val cancel = SInventoryItem(Material.RED_STAINED_GLASS_PANE).setDisplayName("§4§lキャンセル").setCanClick(false).setClickEvent {
            onCancel?.accept(it)
        }

        setItem(43,cancel)

        renderNum()
        displayNumber()

        if (infoItem != null){
            setItem(24, SInventoryItem(infoItem!!).setCanClick(false))
        }

        return true
    }

    fun renderNum(){

        removeItems(0 until  maxDigits)

        for ((index, num) in numItems.entries.withIndex()){
            val item = SInventoryItem(num.value).setDisplayName("$index").setCanClick(false).setClickEvent {
                var str = nowNum.toString()

                if (nowNum == 0L){
                    if (index == 0)return@setClickEvent
                    str = index.toString()
                } else {
                    str += index
                }

                if (str.length > maxDigits){
                    val builder = SStr()
                    for (i in 1..maxDigits){
                        builder.append("9")
                    }
                    nowNum = builder.toString().toLong()
                    displayNumber()
                    return@setClickEvent
                }

                if (str.toInt() > maxNum && maxNum != -1L){
                    nowNum = str.toLong()
                    displayNumber()
                    return@setClickEvent
                }

                nowNum = str.toLong()
                displayNumber()
            }

            setItem(num.key, item)
        }

        val deleteItem = SInventoryItem(Material.TNT).setDisplayName("§c§lクリア").setCanClick(false).setClickEvent {
            nowNum = 0
            displayNumber()
        }
        setItem(48,deleteItem)
    }

    fun renderButton(){
        if (!allowZero){
            if (nowNum == 0L){
                setItem(41,SInventoryItem(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").setCanClick(false))
                return
            }
        }

        val confirm = SInventoryItem(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§a§l確認").setCanClick(false).setClickEvent {
            onConfirm?.accept(nowNum)
        }
        setItem(41,confirm)
    }
}