package tororo1066.nmsutils.v1_21_4

import com.mojang.datafixers.util.Pair
import net.minecraft.ChatFormatting
import net.minecraft.core.Registry
import net.minecraft.core.Rotations
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.*
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.crafting.display.RecipeDisplayId
import net.minecraft.world.phys.Vec3
import net.minecraft.world.scores.*
import net.minecraft.world.scores.criteria.ObjectiveCriteria
import org.bukkit.*
import org.bukkit.craftbukkit.CraftEquipmentSlot
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftEntity
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.craftbukkit.scoreboard.CraftScoreboard
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import tororo1066.nmsutils.SPlayer
import tororo1066.nmsutils.SPlayer.Companion.hiddenEntities
import tororo1066.nmsutils.items.GlowColor
import java.util.Optional

class SPlayerImpl(p: Player): SPlayer, CraftPlayer((p as CraftPlayer).handle.level().craftServer, p.handle) {

    override val bukkitPlayer: Player
        get() = this

    private fun createAddEntityPacket(entity: net.minecraft.world.entity.Entity): ClientboundAddEntityPacket {
        return ClientboundAddEntityPacket(entity, 0, entity.blockPosition())
    }

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
        val packet = ClientboundPlayerRotationPacket(yaw,pitch)
        handle.connection.send(packet)
    }

    override fun damagePacket() {
        val packet = ClientboundEntityEventPacket(handle,2)
        Bukkit.getOnlinePlayers().forEach {
            (it as CraftPlayer).handle.connection.send(packet)
        }
    }

    override fun placeRecipe(recipe: Keyed, isShift: Boolean) {
//        val recipe = (Bukkit.getServer() as CraftServer).server.recipeManager.byKey(ResourceKey.create(Registries.RECIPE, ResourceLocation.fromNamespaceAndPath(
//            recipe.key.namespace,recipe.key.key
//        ))).get()
//        val packet = ServerboundPlaceRecipePacket(handle.containerMenu.containerId, RecipeDisplayId,isShift)
//
//        handle.connection.send(packet)
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

        handle.connection.send(createAddEntityPacket(other))
    }

    override fun spawnFakeInvisibleArmorStand(location: Location, slot: EquipmentSlot, item: ItemStack): Int {

        val armorStand = ArmorStand((location.world as CraftWorld).handle, location.x, location.y, location.z)
        armorStand.setShowArms(true)
        armorStand.isInvisible = true
        armorStand.setLeftArmPose(Rotations(0f,0f,0f))
        armorStand.setRightArmPose(Rotations(0f,0f,0f))
        val spawnPacket = createAddEntityPacket(armorStand)
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
                packet.slots.add(Pair(CraftEquipmentSlot.getNMS(it), CraftItemStack.asNMSCopy(ItemStack(Material.AIR))))
            }
            handle.connection.send(packet)
        } else {
            val packet = ClientboundSetEquipmentPacket(entityId, mutableListOf())
            slots.forEach {
                val item = inventory.getItem(it)
                packet.slots.add(Pair(CraftEquipmentSlot.getNMS(it),CraftItemStack.asNMSCopy(item)))
            }
            handle.connection.send(packet)
        }
    }

    override fun setFakeItem(slot: EquipmentSlot, item: ItemStack) {
        val packet = ClientboundSetEquipmentPacket(entityId, mutableListOf())
        packet.slots.add(Pair(CraftEquipmentSlot.getNMS(slot),CraftItemStack.asNMSCopy(item)))
        handle.connection.send(packet)
    }

    override fun move(x: Double, y: Double, z: Double) {
        handle.move(MoverType.PLAYER, Vec3(x,y,z))
    }

    override fun sendObjective(
        scoreboard: org.bukkit.scoreboard.Scoreboard,
        objectiveName: String,
        displayName: String
    ) {
        val board = (scoreboard as CraftScoreboard).handle
        val objective = Objective(board, objectiveName, ObjectiveCriteria.DUMMY, Component.nullToEmpty(displayName), ObjectiveCriteria.RenderType.INTEGER, false, null)
        val packet = ClientboundSetObjectivePacket(objective, ClientboundSetObjectivePacket.METHOD_REMOVE)
        handle.connection.send(packet)
        val packet1 = ClientboundSetObjectivePacket(objective, ClientboundSetObjectivePacket.METHOD_ADD)
        handle.connection.send(packet1)
        val packet2 = ClientboundSetDisplayObjectivePacket(DisplaySlot.SIDEBAR, objective)
        handle.connection.send(packet2)
    }

    override fun sendScore(objectiveName: String, vararg scores: kotlin.Pair<Int, String>) {
        scores.forEach { (score, name) ->
            val packet = ClientboundSetScorePacket(
                name,
                objectiveName,
                score,
                Optional.empty(),
                Optional.empty()
            )
            handle.connection.send(packet)
        }
    }

    override fun initGlowTeam(nameTagVisibility: String) {
        for (color in GlowColor.values()){
            val team = PlayerTeam(Scoreboard(), color.getTeamName())
                .apply {
                    this.nameTagVisibility = Team.Visibility.byName(nameTagVisibility) ?: Team.Visibility.ALWAYS
                    this.color = ChatFormatting.getByName(color.colorName.uppercase()) ?: ChatFormatting.WHITE
                }
            val removeTeamPacket = ClientboundSetPlayerTeamPacket.createRemovePacket(team)
            val addTeamPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, false)

            handle.connection.send(ClientboundBundlePacket(listOf(removeTeamPacket, addTeamPacket)))
        }
    }

}