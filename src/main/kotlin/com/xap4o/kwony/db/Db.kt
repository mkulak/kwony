package com.xap4o.kwony.db

import com.github.andrewoma.kwery.core.Row
import com.github.andrewoma.kwery.core.SessionFactory
import com.github.andrewoma.kwery.core.dialect.PostgresDialect
import com.github.andrewoma.kwery.core.interceptor.LoggingInterceptor
import com.xap4o.kwony.config.DbConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.intellij.lang.annotations.Language
import javax.sql.DataSource


class Db(val db: SessionFactory) {

    fun batchUpdate(@Language("SQL") sql: String, params: List<Map<String, Any?>>): List<Int> =
            db.use { it.batchUpdate(sql, params) }

    fun <K> batchInsert(@Language("SQL") sql: String, params: List<Map<String, Any?>>, f: (Row) -> K): List<K> =
            db.use { it.batchInsert(sql, params, f = f).map { it.second } }

    fun <K> insert(@Language("SQL") sql: String, params: Map<String, Any?>, f: (Row) -> K): K =
            db.use { it.insert(sql, params, f = f).second }

    fun <R> select(@Language("SQL") sql: String, params: Map<String, Any?> = mapOf(), mapper: (Row) -> R): List<R> =
            db.use { it.select(sql, params, mapper = mapper) }

    fun update(@Language("SQL") sql: String, params: Map<String, Any?>): Int =
            db.use { it.update(sql, params) }


    companion object {
        fun init(config: DbConfig): Db {
            val ds = HikariDataSource().apply {
                jdbcUrl = config.url
                username = config.user
                password = config.password
            }
            migrate(ds)
            return Db(SessionFactory(ds, PostgresDialect(), LoggingInterceptor()))
        }

        private fun migrate(ds: DataSource): Unit {
            val flyway = Flyway()
            flyway.setDataSource(ds)
//        flyway.clean()
            flyway.migrate()
        }
    }
}


