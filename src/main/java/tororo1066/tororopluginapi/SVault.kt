package tororo1066.tororopluginapi

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import java.util.*


class SVault {

    private lateinit var economy: Economy

    init {
        setup()
    }

    fun setup(): Boolean {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null){
            Bukkit.getLogger().warning("Vaultが入っていません")
            return false
        }

        val rsp = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)
        if (rsp == null){
            Bukkit.getLogger().warning("Economyが連携されていません")
            return false
        }

        economy = rsp.provider

        return true
    }

    fun getBalance(uuid: UUID): Double {
        return economy.getBalance(Bukkit.getOfflinePlayer(uuid))
    }

    fun showBalance(uuid: UUID) {
        val p = Bukkit.getPlayer(uuid)?:return
        val money = getBalance(uuid)
        p.sendMessage("§e電子マネー：${money}円")
    }

    fun withdraw(uuid: UUID, amount: Double): Boolean {
        val p = Bukkit.getOfflinePlayer(uuid)
        val resp = economy.withdrawPlayer(p,amount)
        if (resp.transactionSuccess()){
            if (p.isOnline){
                p.player!!.sendMessage("§e電子マネー${amount}円支払いました")
            }
            return true
        }
        return false
    }

    fun deposit(uuid: UUID, amount: Double): Boolean {
        val p = Bukkit.getOfflinePlayer(uuid)
        val resp = economy.depositPlayer(p,amount)
        if (resp.transactionSuccess()){
            if (p.isOnline){
                p.player!!.sendMessage("§e電子マネー${amount}円受取りました")
            }
            return true
        }
        return false
    }
}