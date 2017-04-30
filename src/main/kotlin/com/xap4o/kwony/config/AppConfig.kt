package com.xap4o.kwony.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.time.Duration

data class AppConfig(
        val http: HttpConfig,
        val twitter: TwitterConfig,
        val analyze: AnalyzeConfig,
        val processing: ProcessingConfig,
        val db: DbConfig)

data class HttpConfig(val host: String)

data class DbConfig(val url: String, val user: String, val password: String, val fullConfig: Config)

data class TwitterConfig(
        val host: String,
        val key: String,
        val secret: String,
        val timeout: Duration
)

data class AnalyzeConfig(
        val host: String,
        val timeout: Duration
)

data class ProcessingConfig(
        val interval: Duration
)


fun loadConfig(): AppConfig {
    val c = ConfigFactory.load().getConfig("app")
    val http = c.getConfig("http")
    val twitter = c.getConfig("twitter")
    val analyze = c.getConfig("analyze")
    val proc = c.getConfig("processing")
    val db = c.getConfig("db")
    return AppConfig(
            HttpConfig(
                    http.getString("host")
            ),
            TwitterConfig(
                    twitter.getString("host"),
                    twitter.getString("key"),
                    twitter.getString("secret"),
                    twitter.getDuration("timeout")
            ),
            AnalyzeConfig(
                    analyze.getString("host"),
                    analyze.getDuration("timeout")
            ),
            ProcessingConfig(
                    proc.getDuration("interval")
            ),
            DbConfig(
                    db.getString("url"),
                    db.getString("user"),
                    db.getString("password"),
                    db
            )
    )
}

