package tororo1066.tororopluginapi.sItem

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.banner.Pattern
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BannerMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import tororo1066.tororopluginapi.nbt.ItemStackPersistent
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.UUID


/**
 * ItemStackを継承したクラス。
 * @constructor Material
 */
open class SItem(itemStack: ItemStack) :  ItemStack(itemStack) {

    constructor(material: Material) : this(ItemStack(material))

    constructor(itemStack: ItemStack, amount: Int): this(itemStack){
        this.setItemAmount(amount)
    }

    constructor(material: Material, amount: Int): this(ItemStack(material)){
        this.setItemAmount(amount)
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


    open fun setItemAmount(amount: Int): SItem {
        this.amount = amount
        return this
    }

    /**
     * @param name 名前
     * @return 変更したアイテム
     */
    open fun setDisplayName(name : String): SItem {
        val meta = itemMeta
        meta.setDisplayName(name)
        itemMeta = meta
        return this
    }

    /**
     * @return アイテムの名前
     */
    fun getDisplayName(): String {
        return itemMeta.displayName
    }

    /**
     * @param lore 文字列のリスト
     * @return 変更したアイテム
     */
    open fun setLore(lore : List<String>): SItem {
        val meta = itemMeta
        meta.lore = lore
        itemMeta = meta
        return this
    }

    /**
     * @return loreのリスト なければ空
     */
    fun getStringLore(): List<String> {
        return this.itemMeta.lore?: listOf()
    }


    /**
     * @param lore 追加するlore(リスト)
     * @return 変更したアイテム
     */
    open fun addLore(lore : List<String>): SItem {
        return setLore(getStringLore().toMutableList().apply { addAll(lore) })
    }

    /**
     * @param lore 追加するlore(単一)
     * @return 変更したアイテム
     */
    open fun addLore(lore : String): SItem {
        return addLore(mutableListOf(lore))
    }

    open fun addLore(vararg lore : String): SItem {
        return addLore(lore.toList())
    }


    /**
     * @param csm カスタムモデルデータ
     * @return 変更したアイテム
     */
    open fun setCustomModelData(csm : Int): SItem {
        val meta = itemMeta
        meta.setCustomModelData(csm)
        itemMeta = meta
        return this
    }

    /**
     * @return カスタムモデルデータ
     */
    open fun getCustomModelData(): Int {
        if (!this.itemMeta.hasCustomModelData())return 0
        return itemMeta.customModelData
    }

    /**
     * @param plugin プラグイン
     * @param key 名前
     * @param type PersistentDataType
     * @param value 値
     * @return 変更したアイテム
     */
    open fun <T : Any, Z: Any> setCustomData(plugin: JavaPlugin, key: String, type : PersistentDataType<T,Z>, value: Z): SItem {
        val meta = this.itemMeta
        meta.persistentDataContainer[NamespacedKey(plugin,key),type] = value
        this.itemMeta = meta
        return this
    }

    /**
     * @param plugin プラグイン
     * @param key 名前
     * @param type PersistentDataType
     * @return value
     */
    open fun <T : Any, Z: Any> getCustomData(plugin: JavaPlugin, key: String, type: PersistentDataType<T,Z>): Z? {
        return itemMeta.persistentDataContainer.get(NamespacedKey(plugin, key), type)
    }


    /**
     * @param enchantment エンチャント
     * @param level レベル
     * @return 変更したアイテム
     */
    open fun setEnchantment(enchantment: Enchantment, level: Int): SItem {
        this.addUnsafeEnchantment(enchantment,level)
        return this
    }

    open fun getEnchantment(enchantment: Enchantment): Int? {
        val level = this.itemMeta.getEnchantLevel(enchantment)
        if (level == 0)return null
        return level
    }

    open fun setSkullOwner(uuid: UUID): SItem {
        val meta = itemMeta as SkullMeta
        meta.owningPlayer = Bukkit.getOfflinePlayer(uuid)
        itemMeta = meta
        return this
    }

    open fun addPattern(pattern: Pattern): SItem {
        val meta = itemMeta as BannerMeta
        meta.addPattern(pattern)
        itemMeta = meta
        return this
    }

    /**
     * @return SInventoryItem
     */
    open fun toSInventoryItem(): SInventoryItem {
        return SInventoryItem(this)
    }

    open fun asItemStack(): ItemStack {
        return ItemStack(this)
    }

    override fun clone(): SItem {
        return super.clone() as SItem
    }

    /**
     * @return  変換後のBase64
     */
    fun toBase64(): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)
            dataOutput.writeInt(1)
            dataOutput.writeObject(ItemStack(this))
            dataOutput.close()
            Base64Coder.encodeLines(outputStream.toByteArray())

        } catch (e: Exception) {
            throw IllegalStateException("Failed itemStack to Base64",e)
        }

    }






}