package tororo1066.tororopluginapi.sItem

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.banner.Pattern
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BannerMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.SStr.Companion.toSStr
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.UUID


/**
 * ItemStackを継承したクラス。
 * @constructor Material
 */
@Suppress("DEPRECATION")
open class SItem(protected var itemStack: ItemStack): Cloneable {

    constructor(material: Material) : this(ItemStack(material))

    constructor(itemStack: ItemStack, amount: Int): this(itemStack){
        this.setItemAmount(amount)
    }

    constructor(material: Material, amount: Int): this(ItemStack(material)){
        this.setItemAmount(amount)
    }

    init {
        itemStack = itemStack.clone()
    }

    companion object{

        /**
         * @param data Base64
         * @return SItem
         */
        fun fromBase64(data: String): SItem? {
            return try {

                val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
                val dataInput = BukkitObjectInputStream(inputStream)
                val items = arrayOfNulls<ItemStack>(dataInput.readInt())

                for (i in items.indices){
                    items[i] = dataInput.readObject() as ItemStack
                }

                dataInput.close()
                return SItem(items[0]!!)
            } catch (e: Exception) {
                null
            }
        }

        fun ItemStack.toSItem(): SItem {
            return SItem(this)
        }

        fun List<ItemStack>.toBase64Items(): String {
            try {
                val outputStream = ByteArrayOutputStream()
                val dataOutput = BukkitObjectOutputStream(outputStream)

                // Write the size of the inventory
                dataOutput.writeInt(this.size)

                // Save every element in the list
                for (i in this.indices) {
                    dataOutput.writeObject(this[i])
                }

                // Serialize that array
                dataOutput.close()
                return Base64Coder.encodeLines(outputStream.toByteArray())

            } catch (e: Exception) {
                throw IllegalStateException("Failed ItemStack to Base64.",e)
            }
        }

        fun String.toItems(): MutableList<ItemStack> {
            try {

                val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(this))
                val dataInput = BukkitObjectInputStream(inputStream)
                val items = arrayOfNulls<ItemStack>(dataInput.readInt())

                // Read the serialized inventory
                for (i in items.indices) {
                    items[i] = dataInput.readObject() as ItemStack
                }

                dataInput.close()

                val mutableList = mutableListOf<ItemStack>()
                items.forEach {
                    if (it != null) mutableList.add(it)
                }

                return mutableList
            } catch (e: Exception) {
                throw IllegalStateException("Failed Base64 to ItemStack List.",e)
            }
        }

        fun String.toSItems(): MutableList<SItem> {
            try {

                val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(this))
                val dataInput = BukkitObjectInputStream(inputStream)
                val items = arrayOfNulls<ItemStack>(dataInput.readInt())

                // Read the serialized inventory
                for (i in items.indices) {
                    items[i] = dataInput.readObject() as ItemStack
                }

                dataInput.close()

                val mutableList = mutableListOf<SItem>()
                items.forEach {
                    if (it != null) mutableList.add(SItem(it))
                }

                return mutableList
            } catch (e: Exception) {
                throw IllegalStateException("Failed Base64 to ItemStack List.",e)
            }
        }

    }

    private fun getMeta(): ItemMeta {
        return if (itemStack.hasItemMeta()) itemStack.itemMeta else Bukkit.getItemFactory().getItemMeta(itemStack.type)
    }


    open fun setItemAmount(amount: Int): SItem {
        itemStack.amount = amount
        return this
    }

    /**
     * @param name 名前
     * @return 変更したアイテム
     */
    open fun setDisplayName(name : String): SItem {
        val meta = getMeta()
        meta.setDisplayName(name)
        itemStack.itemMeta = meta
        return this
    }

    open fun setSStrDisplayName(sStr: SStr): SItem {
        val meta = getMeta()
        if (SStr.isPaper()){
            meta.displayName(sStr.toPaperComponent())
        } else {
            meta.setDisplayNameComponent(sStr.toBukkitComponent())
        }
        itemStack.itemMeta = meta
        return this
    }

    /**
     * @return アイテムの名前
     */
    fun getDisplayName(): String {
        val meta = getMeta()
        return if (meta.hasDisplayName()) meta.displayName else ""
    }

    /**
     * @param lore 文字列のリスト
     * @return 変更したアイテム
     */
    open fun setLore(lore : List<String>): SItem {
        val meta = getMeta()
        meta.lore = lore
        itemStack.itemMeta = meta
        return this
    }

    open fun setLore(vararg lore : String): SItem {
        return setLore(lore.toList())
    }

    open fun setSStrLore(sStr: List<SStr>): SItem {
        val meta = getMeta()
        if (SStr.isPaper()){
            meta.lore(sStr.map { it.toPaperComponent() })
        } else {
            meta.loreComponents = sStr.map { it.toBukkitComponent() }
        }
        itemStack.itemMeta = meta
        return this
    }

    /**
     * @return loreのリスト なければ空
     */
    fun getStringLore(): List<String> {
        val meta = getMeta()
        return if (meta.hasLore()) meta.lore?: listOf() else listOf()
    }

    fun getSStrLore(): List<SStr> {
        return if (SStr.isPaper()){
            this.getMeta().lore()?.map { it.toSStr() }?: listOf()
        } else {
            this.getMeta().loreComponents?.map { it.toSStr() }?: listOf()
        }
    }


    /**
     * @param lore 追加するlore(リスト)
     * @return 変更したアイテム
     */
    open fun addLore(lore : List<String>): SItem {
        return setLore(getStringLore().toMutableList().apply { addAll(lore) })
    }

    open fun addLore(vararg lore : String): SItem {
        return addLore(lore.toList())
    }

    open fun addSStrLore(sStr: List<SStr>): SItem {
        return setSStrLore(getSStrLore().toMutableList().apply { addAll(sStr) })
    }

    open fun addSStrLore(vararg sStr: SStr): SItem {
        return addSStrLore(sStr.toList())
    }


    /**
     * @param cmd カスタムモデルデータ
     * @return 変更したアイテム
     */
    open fun setCustomModelData(cmd : Int): SItem {
        val meta = getMeta()
        meta.setCustomModelData(cmd)
        itemStack.itemMeta = meta
        return this
    }

    /**
     * @return カスタムモデルデータ
     */
    open fun getCustomModelData(): Int {
        val meta = getMeta()
        if (!meta.hasCustomModelData())return 0
        return meta.customModelData
    }

    /**
     * @param plugin プラグイン
     * @param key 名前
     * @param type PersistentDataType
     * @param value 値
     * @return 変更したアイテム
     */
    open fun <T : Any, Z: Any> setCustomData(plugin: JavaPlugin, key: String, type : PersistentDataType<T,Z>, value: Z): SItem {
        val meta = this.getMeta()
        meta.persistentDataContainer[NamespacedKey(plugin,key),type] = value
        this.itemStack.itemMeta = meta
        return this
    }

    /**
     * @param plugin プラグイン
     * @param key 名前
     * @param type PersistentDataType
     * @return value
     */
    open fun <T : Any, Z: Any> getCustomData(plugin: JavaPlugin, key: String, type: PersistentDataType<T,Z>): Z? {
        return getMeta().persistentDataContainer.get(NamespacedKey(plugin, key), type)
    }


    /**
     * @param enchantment エンチャント
     * @param level レベル
     * @return 変更したアイテム
     */
    open fun setEnchantment(enchantment: Enchantment, level: Int): SItem {
        itemStack.addUnsafeEnchantment(enchantment,level)
        return this
    }

    open fun getEnchantment(enchantment: Enchantment): Int? {
        val level = getMeta().getEnchantLevel(enchantment)
        if (level == 0)return null
        return level
    }

    open fun setSkullOwner(uuid: UUID): SItem {
        val meta = (getMeta()) as SkullMeta
        meta.owningPlayer = Bukkit.getOfflinePlayer(uuid)
        itemStack.itemMeta = meta
        return this
    }

    open fun addPattern(pattern: Pattern): SItem {
        val meta = (getMeta()) as BannerMeta
        meta.addPattern(pattern)
        itemStack.itemMeta = meta
        return this
    }

    open fun addItemFlags(vararg flags: ItemFlag): SItem {
        val meta = getMeta()
        meta.addItemFlags(*flags)
        itemStack.itemMeta = meta
        return this
    }

    open fun removeItemFlags(vararg flags: ItemFlag): SItem {
        val meta = getMeta()
        meta.removeItemFlags(*flags)
        itemStack.itemMeta = meta
        return this
    }

    /**
     * @return SInventoryItem
     */
    open fun toSInventoryItem(): SInventoryItem {
        return SInventoryItem(this)
    }

    open fun build(): ItemStack {
        return itemStack
    }

    public override fun clone(): SItem {
        return SItem(itemStack.clone())
    }

    /**
     * @return  変換後のBase64
     */
    fun toBase64(): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)
            dataOutput.writeInt(1)
            dataOutput.writeObject(build())
            dataOutput.close()
            Base64Coder.encodeLines(outputStream.toByteArray())

        } catch (e: Exception) {
            throw IllegalStateException("Failed itemStack to Base64",e)
        }

    }






}