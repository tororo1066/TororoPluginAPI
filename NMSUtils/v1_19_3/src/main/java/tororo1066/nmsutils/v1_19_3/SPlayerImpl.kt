package tororo1066.nmsutils.v1_19_3

import com.mojang.datafixers.util.Pair
import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.inventory.MenuType
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_19_R2.CraftEquipmentSlot
import org.bukkit.craftbukkit.v1_19_R2.CraftServer
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import tororo1066.nmsutils.SPlayer
import tororo1066.nmsutils.SPlayer.Companion.hiddenEntities
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.forEach
import kotlin.collections.toSet

class SPlayerImpl(p: Player): SPlayer, CraftPlayer((p as CraftPlayer).handle.level.craftServer, p.handle) {

    override fun updateInventoryTitle(inv: Inventory, title: String) {
        val con = when(inv.size){
            9-> MenuType.GENERIC_9x1
            18-> MenuType.GENERIC_9x2
            27-> MenuType.GENERIC_9x3
            36-> MenuType.GENERIC_9x4
            45-> MenuType.GENERIC_9x5
            54-> MenuType.GENERIC_9x6
            else-> MenuType.GENERIC_9x6
        }
        val packet = ClientboundOpenScreenPacket(handle.containerMenu.containerId, con, Component.nullToEmpty(null).copy().append(title))
        handle.connection.send(packet)
        handle.initMenu(handle.containerMenu)
    }

    override fun pickUpPlayer(item: Item) {
        val packet = ClientboundTakeItemEntityPacket(item.entityId,entityId,item.itemStack.amount)
        Bukkit.getOnlinePlayers().forEach {
            (it as CraftPlayer).handle.connection.send(packet)
        }
    }

    override fun moveRotation(yaw: Float, pitch: Float) {
        val packet = ClientboundPlayerPositionPacket(0.0,0.0,0.0,yaw,pitch,
            ClientboundPlayerPositionPacket.RelativeArgument.values().toSet(),0,false)
        handle.connection.send(packet)
    }

    override fun damagePacket() {
        val packet = ClientboundEntityEventPacket(handle,2)
        Bukkit.getOnlinePlayers().forEach {
            (it as CraftPlayer).handle.connection.send(packet)
        }
    }

    override fun placeRecipe(recipe: Keyed, isShift: Boolean) {
        val packet = ServerboundPlaceRecipePacket(handle.containerMenu.containerId,(Bukkit.getServer() as CraftServer).server.recipeManager.byKey(
            ResourceLocation(recipe.key.key,recipe.key.namespace)
        ).get(),isShift)

        handle.connection.send(packet)
    }

    override fun hideEntity(entity: Entity, hiddenPlayerList: Boolean) {
        if (this == entity)return
        if (!hiddenEntities.containsKey(uniqueId)){
            hiddenEntities[uniqueId] = arrayListOf()
        }
        if (hiddenEntities[uniqueId]!!.contains(entity.uniqueId)){
            return
        }
        hiddenEntities[uniqueId]!!.add(entity.uniqueId)

        val other: net.minecraft.world.entity.Entity =
            (entity as CraftEntity).handle
        unregisterEntity(other, hiddenPlayerList)
    }

    private fun unregisterEntity(other: net.minecraft.world.entity.Entity, hiddenPlayerList: Boolean) {
        handle.connection.send(ClientboundRemoveEntitiesPacket(other.id))

        if (other is ServerPlayer) {
            if (hiddenPlayerList && other.sentListPacket) {
                handle.connection.send(
                    ClientboundPlayerInfoRemovePacket(
                        listOf(other.uuid)
                    ))
            }
        }
    }

    override fun showEntity(entity: Entity) {
        if (this == entity)return
        if (!hiddenEntities.containsKey(uniqueId))return
        if (!hiddenEntities[uniqueId]!!.contains(entity.uniqueId))return
        hiddenEntities[uniqueId]!!.remove(entity.uniqueId)

        val other: net.minecraft.world.entity.Entity =
            (entity as CraftEntity).handle
        registerEntity(other)
    }

    private fun registerEntity(other: net.minecraft.world.entity.Entity) {

        if (other is ServerPlayer) {
            handle.connection.send(
                ClientboundPlayerInfoUpdatePacket(
                    ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                    other
                ))
        }

        handle.connection.send(ClientboundAddEntityPacket(other))
    }

    override fun spawnFakeInvisibleArmorStand(location: Location, slot: EquipmentSlot, item: ItemStack): Int {

        val armorStand = ArmorStand((location.world as CraftWorld).handle, location.x, location.y, location.z)
        armorStand.isShowArms = true
        armorStand.isInvisible = true
        armorStand.setLeftArmPose(Rotations(0f,0f,0f))
        armorStand.setRightArmPose(Rotations(0f,0f,0f))
        val spawnPacket = ClientboundAddEntityPacket(armorStand)
        handle.connection.send(spawnPacket)
        handle.connection.send(ClientboundSetEntityDataPacket(armorStand.id, armorStand.entityData.packDirty()?: listOf()))
        handle.connection.send(ClientboundSetEquipmentPacket(armorStand.id, mutableListOf(Pair(CraftEquipmentSlot.getNMS(slot), CraftItemStack.asNMSCopy(item)))))
        return armorStand.id
    }

    override fun removeFakeInvisibleArmorStand(entityId: Int) {
        val packet = ClientboundRemoveEntitiesPacket(entityId)
        handle.connection.send(packet)
    }

    override fun invisibleItems(slots: List<EquipmentSlot>, invisible: Boolean) {
        if (invisible){
            val packet = ClientboundSetEquipmentPacket(entityId, mutableListOf())
            slots.forEach {
                packet.slots.add(Pair(CraftEquipmentSlot.getNMS(it),null))
            }
            handle.connection.send(packet)
        } else {
            val packet = ClientboundSetEquipmentPacket(entityId, mutableListOf())
            slots.forEach {
                packet.slots.add(Pair(CraftEquipmentSlot.getNMS(it),CraftItemStack.asNMSCopy(inventory.getItem(it))))
            }
            handle.connection.send(packet)
        }
    }

}