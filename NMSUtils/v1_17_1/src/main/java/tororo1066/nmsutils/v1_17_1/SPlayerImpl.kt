package tororo1066.nmsutils.v1_17_1

import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.inventory.MenuType
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.craftbukkit.v1_17_R1.CraftServer
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import tororo1066.nmsutils.SPlayer
import tororo1066.nmsutils.SPlayer.Companion.hiddenEntities

class SPlayerImpl(p: Player): SPlayer, CraftPlayer((p as CraftPlayer).handle.level.craftServer, p.handle){

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
            ResourceLocation(recipe.key.key,recipe.key.namespace)).get(),isShift)

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
                    ClientboundPlayerInfoPacket(
                        ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER,
                        other
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
                ClientboundPlayerInfoPacket(
                    ClientboundPlayerInfoPacket.Action.ADD_PLAYER,
                    other
                ))
        }

        handle.connection.send(ClientboundAddEntityPacket(other))
    }
}