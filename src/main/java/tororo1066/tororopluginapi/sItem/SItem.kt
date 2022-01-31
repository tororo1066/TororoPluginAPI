package tororo1066.tororopluginapi.sItem

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import tororo1066.tororopluginapi.sInventory.SInventoryItem

open class SItem(itemStack: ItemStack) : ItemStack(itemStack) {

    constructor(material: Material) : this(ItemStack(material)){

    }

    constructor(sItem: SItem) : this(sItem as ItemStack){

    }

    /**
     * @param name 名前
     * @return 変更したアイテム
     */
    fun setDisplayName(name : String): SItem {
        val meta = itemMeta?:return this
        meta.setDisplayName(name)
        itemMeta = meta
        return this
    }

    /**
     * @return アイテムの名前
     */
    fun getDisplayName(): String {
        return itemMeta?.displayName?:return ""
    }

    /**
     * @param lore 文字列のリスト
     * @return 変更したアイテム
     */
    fun setLore(lore : List<String>): SItem {
        val meta = itemMeta?:return this
        meta.lore = lore
        itemMeta = meta
        return this
    }

    /**
     * @return loreのリスト なければ空
     */
    fun getLore(): List<String> {
        return this.itemMeta?.lore?: listOf()
    }


    /**
     * @param lore 追加するlore(リスト)
     * @return 変更したアイテム
     */
    fun addLore(lore : List<String>): SItem {
        return setLore(getLore().toMutableList().apply { addAll(lore) })
    }

    /**
     * @param lore 追加するlore(単一)
     * @return 変更したアイテム
     */
    fun addLore(lore : String): SItem {
        return addLore(mutableListOf(lore))
    }


    /**
     * @param csm カスタムモデルデータ
     * @return 変更したアイテム
     */
    fun setCustomModelData(csm : Int): SItem {
        val meta = itemMeta?:return this
        meta.setCustomModelData(csm)
        itemMeta = meta
        return this
    }

    /**
     * @return カスタムモデルデータ
     */
    fun getCustomModelData(): Int {
        return itemMeta?.customModelData?:0
    }

    fun toSInventoryItem(): SInventoryItem {
        return SInventoryItem(this)
    }






}