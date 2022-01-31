package tororo1066.tororopluginapi.sCommand

import org.bukkit.Bukkit

enum class SCommandArgType {
    STRING,
    INT,
    LONG,
    DOUBLE,
    ONLINE_PLAYER,
    WORLD,
    BOOLEAN;

    fun match(s : String): Boolean {
        when(this){
            BOOLEAN->{
                if ("true".equals(s,true) || "false".equals(s,true)){
                    return true
                }
                return false
            }
            INT->{
                return s.toIntOrNull() != null
            }
            LONG->{
                return s.toLongOrNull() != null
            }
            DOUBLE->{
                return s.toDoubleOrNull() != null
            }
            WORLD->{
                return Bukkit.getWorld(s) != null
            }
            ONLINE_PLAYER->{
                return Bukkit.getPlayer(s) != null
            }
            STRING->return true
        }
    }
}