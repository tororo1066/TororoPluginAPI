package tororo1066.nmsutils

import org.bukkit.Bukkit

interface SNmsHandler {
    companion object {
        @JvmStatic
        fun newInstance(): SNms {
            val version = Bukkit.getServer().bukkitVersion.split("-")[0].replace(".","_")
            return when(version) {
                "1_17_1" -> tororo1066.nmsutils.v1_17_1.SNmsImpl()
                "1_19_2" -> tororo1066.nmsutils.v1_19_2.SNmsImpl()
                "1_19_3" -> tororo1066.nmsutils.v1_19_3.SNmsImpl()
                "1_20_1" -> tororo1066.nmsutils.v1_20_1.SNmsImpl()
                else -> throw UnsupportedOperationException("SNms not supported mc_version ${Bukkit.getServer().minecraftVersion}.")
            }
        }
    }
}