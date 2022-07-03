package tororo1066.tororopluginapi.sItem

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


/**
 * ItemStackを継承したクラス。
 * @constructor Material
 */
open class SItem(itemStack: ItemStack) :  ItemStack(itemStack) {

    constructor(material: Material) : this(ItemStack(material)){

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
                val item = dataInput.readObject() as ItemStack

                dataInput.close()
                return SItem(item)
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
                dataOutput.writeInt(this.size)
                for (i in this.indices){
                    dataOutput.writeObject(this[i])
                }
                dataOutput.close()
                return Base64Coder.encodeLines(outputStream.toByteArray())

            } catch (e: Exception) {
                throw IllegalStateException("Failed ItemStack to Base64.",e)
            }
        }

        @JvmName("toBase64ItemsSItem")
        fun List<SItem>.toBase64Items(): String {
            try {
                val outputStream = ByteArrayOutputStream()
                val dataOutput = BukkitObjectOutputStream(outputStream)
                dataOutput.writeInt(this.size)
                for (i in this){
                    dataOutput.writeObject(i as ItemStack)
                }
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

                for (i in items.indices){
                    items[i] = dataInput.readObject() as ItemStack
                }

                val mutableList = mutableListOf<ItemStack>()
                items.forEach {
                    if (it != null) mutableList.add(it)
                }

                dataInput.close()
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

                for (i in items.indices){
                    items[i] = dataInput.readObject() as ItemStack
                }

                val mutableList = mutableListOf<SItem>()
                items.forEach {
                    if (it != null) mutableList.add(SItem(it))
                }

                dataInput.close()
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
    open fun setLore(lore : List<String>): SItem {
        val meta = itemMeta?:return this
        meta.lore = lore
        itemMeta = meta
        return this
    }

    /**
     * @return loreのリスト なければ空
     */
    fun getStringLore(): List<String> {
        return this.itemMeta?.lore?: listOf()
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


    /**
     * @param csm カスタムモデルデータ
     * @return 変更したアイテム
     */
    open fun setCustomModelData(csm : Int): SItem {
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

    /**
     * @param plugin プラグイン
     * @param key 名前
     * @param type PersistentDataType
     * @param value 値
     * @return 変更したアイテム
     */
    open fun<T : Any> setCustomData(plugin: JavaPlugin, key: String, type : PersistentDataType<T,T>, value: T): SItem {
        val meta = this.itemMeta?:return this
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
    fun<T : Any> getCustomData(plugin: JavaPlugin, key: String, type: PersistentDataType<T,T>): T? {
        return itemMeta?.persistentDataContainer?.get(NamespacedKey(plugin, key), type)
    }


    /**
     * @param enchantment エンチャント
     * @param level レベル
     * @return 変更したアイテム
     */
    open fun setEnchantment(enchantment: Enchantment, level: Int): SItem {
        val meta = this.itemMeta?:return this
        meta.addEnchant(enchantment,level,true)
        this.itemMeta = meta
        return this
    }

    fun getEnchantment(enchantment: Enchantment): Int? {
        val level = this.itemMeta?.getEnchantLevel(enchantment)
        if (level == 0)return null
        return level
    }

    /**
     * @return SInventoryItem
     */
    fun toSInventoryItem(): SInventoryItem {
        return SInventoryItem(this)
    }

    /**
     * @return  変換後のBase64
     */
    fun toBase64(): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)
            dataOutput.writeInt(1)
            dataOutput.writeObject(this as ItemStack)
            dataOutput.close()
            Base64Coder.encodeLines(outputStream.toByteArray())

        } catch (e: Exception) {
            throw IllegalStateException("Failed itemStack to Base64",e)
        }

    }






}