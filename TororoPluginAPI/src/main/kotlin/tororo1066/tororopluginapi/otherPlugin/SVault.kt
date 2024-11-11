package tororo1066.tororopluginapi.otherPlugin

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import java.util.*


/**
 * Vaultを楽に使えるクラス
 */
class SVault {

    private lateinit var economy: Economy

    /**
     * default true
     */
    var yenMoney = true

    init {
        setup()
    }

    private fun setup(): Boolean {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")){
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


    /**
     * 金額を確認する
     * @param uuid 確認するプレイヤーのUUID
     */
    fun getBalance(uuid: UUID): Double {
        return economy.getBalance(Bukkit.getOfflinePlayer(uuid))
    }

    /**
     * 金額を表示させる
     * @param uuid 表示させるプレイヤーのUUID
     */
    fun showBalance(uuid: UUID) {
        val p = Bukkit.getPlayer(uuid)?:return
        val money = getBalance(uuid)
        p.sendMessage(showBalanceMessage(p.locale,money))

    }

    /**
     * 指定した金額出金する
     * @param uuid 出金させるプレイヤーのUUID
     * @param amount 金額
     * @return 成功か成功してないか
     */
    fun withdraw(uuid: UUID, amount: Double): Boolean {
        val p = Bukkit.getOfflinePlayer(uuid)
        val resp = economy.withdrawPlayer(p,amount)
        if (resp.transactionSuccess()){
            if (p.isOnline){
                p.player!!.sendMessage(withdrawMessage(p.player!!.locale,amount))
            }
            return true
        }
        return false
    }

    /**
     * 指定した金額入金する
     * @param uuid 入金させるプレイヤーのUUID
     * @param amount 金額
     * @return 成功か成功してないか
     */
    fun deposit(uuid: UUID, amount: Double): Boolean {
        val p = Bukkit.getOfflinePlayer(uuid)
        val resp = economy.depositPlayer(p,amount)
        if (resp.transactionSuccess()){
            if (p.isOnline){
                p.player!!.sendMessage(depositMessage(p.player!!.locale,amount))
            }
            return true
        }
        return false
    }

    private fun depositMessage(locale: String, amount: Double): String {
        return when(locale){
            "ja_jp"->
                if (yenMoney) "§e${amount}円受取りました" else "§e電子マネー${amount}円受取りました"
            else-> "§eDeposited $amount$"
        }
    }

    private fun withdrawMessage(locale: String, amount: Double): String {
        return when(locale){
            "ja_jp"->
                if (yenMoney) "§e${amount}円支払いました" else "§e電子マネー${amount}円支払いました"
            else-> "§eWithdrawal $amount$"
        }
    }

    private fun showBalanceMessage(locale: String, amount: Double): String {
        return when(locale){
            "ja_jp"->
                if (yenMoney) "§e所持金：${amount}円" else "§e電子マネー${amount}円"
            else-> "§eBalance $amount$"
        }
    }


}