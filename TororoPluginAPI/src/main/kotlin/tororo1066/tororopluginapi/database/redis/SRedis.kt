package tororo1066.tororopluginapi.database.redis

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class SRedis(config: ConfigurationSection) {

    private var host = "localhost"
    private var port = 6379
    private var username: String? = null
    private var password: String? = null
    private var database = 0
    private var useSSL = false

    private var sentinelMode = false
    private var masterName: String? = null
    private var sentinels = mutableListOf<SentinelInfo>()

    val client: RedisClient

    private data class SentinelInfo(
        val host: String,
        val port: Int,
        val password: String?
    )

    init {
        host = config.getString("host","localhost")!!
        port = config.getInt("port",6379)
        username = config.getString("username",null)
        password = config.getString("password",null)
        database = config.getInt("database",0)
        useSSL = config.getBoolean("useSSL",false)

        val sentinelSection = config.getConfigurationSection("sentinel")
        if (sentinelSection != null) {
            sentinelMode = sentinelSection.getBoolean("enabled",false)
            masterName = sentinelSection.getString("masterName",null)
            val sentinelsList = sentinelSection.getMapList("sentinels")
            sentinelsList.forEach {
                val host = it["host"] as? String ?: return@forEach
                val port = (it["port"] as? Int) ?: 26379
                val sentinelPassword = it["password"] as? String
                sentinels.add(SentinelInfo(host, port, sentinelPassword))
            }
        }

        client = RedisClient.create(RedisURI.builder().apply {
            if (sentinelMode) {
                if (masterName == null) {
                    throw IllegalArgumentException("Sentinel mode is enabled but masterName is not set.")
                }
                withSentinelMasterId(masterName)
                sentinels.forEach {
                    if (it.password != null) {
                        withSentinel(it.host, it.port, it.password)
                    } else {
                        withSentinel(it.host, it.port)
                    }
                }
            } else {
                withHost(host)
                withPort(port)
            }
            if (password != null) {
                if (username != null) {
                    withAuthentication(username, password)
                } else {
                    withPassword(password)
                }
            }
            withDatabase(database)
            withSsl(useSSL)
        }.build())
    }

    companion object {
        private fun loadConfigSection(plugin: JavaPlugin, configFile: String, configPath: String): ConfigurationSection {
            val file = File(plugin.dataFolder, configFile)
            if (!file.exists()) {
                throw IllegalArgumentException("[SRedis] Config file $configFile does not exist.")
            }
            val config = YamlConfiguration.loadConfiguration(file)
            return config.getConfigurationSection(configPath)
                ?: throw IllegalArgumentException("[SRedis] Config path $configPath does not exist in $configFile.")
        }
    }

    constructor(
        plugin: JavaPlugin,
        configFile: String = "config.yml",
        configPath: String = "redis"
    ) : this(loadConfigSection(plugin, configFile, configPath))
}