package tororo1066.nmsutils.v1_19_3

import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.inventory.MenuType
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_19_R2.CraftServer
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import tororo1066.nmsutils.SPlayer
import java.util.*
import java.util.List
import kotlin.collections.ArrayList
import kotlin.collections.forEach
import kotlin.collections.toSet

class SPlayerImpl(p: Player): SPlayer, CraftPlayer((p as CraftPlayer).handle.level.craftServer, p.handle) {

    val hiddenEntities = ArrayList<UUID>()

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
        if (this == entity) return
        if (hiddenEntities.contains(entity.uniqueId))return
        hiddenEntities.add(entity.uniqueId)

        val other: net.minecraft.world.entity.Entity =
            (entity as CraftEntity).handle
        unregisterEntity(other, hiddenPlayerList)
    }

    private fun unregisterEntity(other: net.minecraft.world.entity.Entity, hiddenPlayerList: Boolean) {
        val tracker = (handle.level as ServerLevel).getChunkSource().chunkMap
        val entry = tracker.entityMap[other.id]
        entry.removePlayer(handle)

        if (other is ServerPlayer) {
            if (hiddenPlayerList && other.sentListPacket) {
                handle.connection.send(ClientboundPlayerInfoRemovePacket(listOf(other.uuid)))
            }
        }
    }

    override fun showEntity(entity: Entity) {
        if (this == entity)return
        if (!hiddenEntities.contains(entity.uniqueId))return
        hiddenEntities.remove(entity.uniqueId)

        val other: net.minecraft.world.entity.Entity =
            (entity as CraftEntity).handle
        registerEntity(other)
    }

    private fun registerEntity(other: net.minecraft.world.entity.Entity) {
        val tracker = (handle.level as ServerLevel).getChunkSource().chunkMap

        if (other is ServerPlayer) {
            handle.connection.send(
                ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(listOf(other))
            )
        }

        val entry = tracker.entityMap[other.id]
        if (entry != null && !entry.seenBy.contains(handle.connection)) {
            entry.updatePlayer(handle)
        }
    }

}