package com.xap4o.kwony.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.time.Duration

data class AppConfig(val http: HttpConfig, val processing: ProcessingConfig, val db: DbConfig)

data class HttpConfig(val host: String, val port: Int)

data class DbConfig(val url: String, val user: String, val password: String, val fullConfig: Config)

data class ProcessingConfig(
        val interval: Duration,
        val twitterHost: String,
        val twitterKey: String,
        val twitterSecret: String,
        val timeout: Duration,
        val analyzeHost: String
)


fun loadConfig(): AppConfig {
    val c = ConfigFactory.load().getConfig("app")
    val http = c.getConfig("http")
    val proc = c.getConfig("processing")
    val db = c.getConfig("db")
    return AppConfig(
            HttpConfig(
                    http.getString("host"),
                    http.getInt("port")
            ),
            ProcessingConfig(
                    proc.getDuration("interval"),
                    proc.getString("twitterHost"),
                    proc.getString("twitterKey"),
                    proc.getString("twitterSecret"),
                    proc.getDuration("timeout"),
                    proc.getString("analyzeHost")
            ),
            DbConfig(
                    db.getString("url"),
                    db.getString("user"),
                    db.getString("password"),
                    db
            )
    )
}

