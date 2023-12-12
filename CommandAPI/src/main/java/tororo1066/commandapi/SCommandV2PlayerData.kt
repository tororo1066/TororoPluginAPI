package tororo1066.commandapi

import org.bukkit.entity.Player

class SCommandV2PlayerData(override val sender: Player, label: String, args: CommandArguments): SCommandV2Data(sender, label, args)