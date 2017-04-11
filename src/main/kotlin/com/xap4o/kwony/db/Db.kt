package com.xap4o.kwony.db

import com.github.andrewoma.kwery.core.SessionFactory
import com.github.andrewoma.kwery.core.dialect.PostgresDialect
import com.github.andrewoma.kwery.core.interceptor.LoggingInterceptor
import com.xap4o.kwony.config.DbConfig
import com.xap4o.kwony.processing.AnalyzeResult
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway


object Db {
    fun migrate(config: DbConfig): Unit {
        val flyway = Flyway()
        flyway.setDataSource(config.url, config.user, config.password)
//        flyway.clean()
        flyway.migrate()
    }

    fun initSessionFactory(config: DbConfig): SessionFactory {
        val ds = HikariDataSource().apply {
            jdbcUrl = config.url
            username = config.user
            password = config.password
        }
        return SessionFactory(ds, PostgresDialect(), LoggingInterceptor())
    }
}

class AnalyzeResultDb(val db: SessionFactory) {
    fun persist(r: AnalyzeResult): Int =
            db.use {
                it.insert(
                        sql = """INSERT INTO analyze_result(total, positive, negative, errors, duration) VALUES (:total, :positive, :negative, :errors, :duration)""",
                        parameters = mapOf("total" to r.total, "positive" to r.positive, "negative" to r.negative, "errors" to r.errors, "duration" to r.duration),
                        f = { it.int("id") }
                ).second
            }
}

class SearchKeywordsDb(val db: SessionFactory) {
    fun getAll(): List<String> =
            db.use {
                it.select("""SELECT value FROM search_keyword ORDER BY id""", mapper = { row -> row.string("value") })
            }

    fun persist(keyword: String): Int =
            db.use {
                it.insert(
                        sql = """INSERT INTO search_keyword(value) VALUES (:keyword)""",
                        parameters = mapOf("keyword" to keyword),
                        f = {
                            it.int("id")
                        }
                ).second
            }

    fun delete(keyword: String): Int =
            db.use {
                it.update("""DELETE FROM search_keyword WHERE value=:keyword""", mapOf("keyword" to keyword))
            }
}


