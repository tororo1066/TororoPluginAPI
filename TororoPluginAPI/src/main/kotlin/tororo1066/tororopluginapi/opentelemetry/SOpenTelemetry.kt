package tororo1066.tororopluginapi.opentelemetry

import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.logs.Logger
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.logs.SdkLoggerProvider
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.java.JavaPlugin

class SOpenTelemetry {

    private var endpoint: String? = null
    private var serviceName: String? = null

    private val openTelemetry: OpenTelemetry by lazy {
        val endpoint = endpoint ?: throw IllegalStateException("OpenTelemetry endpoint is not configured")
        val exporter = OtlpHttpLogRecordExporter.builder()
            .setEndpoint(endpoint)
            .build()

        val loggerProvider = SdkLoggerProvider.builder()
            .addLogRecordProcessor(BatchLogRecordProcessor.builder(exporter).build())
            .build()

        OpenTelemetrySdk.builder()
            .setLoggerProvider(loggerProvider)
            .build()
    }

    val logger: Logger by lazy {
        val serviceName = serviceName ?: throw IllegalStateException("OpenTelemetry service name is not configured")
        openTelemetry.logsBridge.get(serviceName)
    }

    constructor(
        plugin: JavaPlugin,
        configFile: ConfigurationSection = plugin.config,
        configPath: String = "opentelemetry"
    ) {
        val otelConfig = configFile.getConfigurationSection(configPath) ?: return
        endpoint = otelConfig.getString("endpoint")
        serviceName = otelConfig.getString("service-name") ?: plugin.name
    }
}