package com.xap4o.kwony.db

import com.xap4o.kwony.utils.Try

interface SearchKeywordsDb {
    fun getAll(): Try<List<String>>
    fun persist(keyword: String): Try<Int>
    fun delete(keyword: String): Try<Int>
}


class PostgresSearchKeywordsDb(val db: Db) : SearchKeywordsDb {
    override fun getAll(): Try<List<String>> =
            db.select("""SELECT value FROM search_keyword ORDER BY id""", mapper = { row -> row.string("value") })

    override fun persist(keyword: String): Try<Int> =
            db.insert("""INSERT INTO search_keyword(value) VALUES (:keyword)""",
                    mapOf("keyword" to keyword),
                    f = { it.int("id") })

    override fun delete(keyword: String): Try<Int> =
            db.update("""DELETE FROM search_keyword WHERE value=:keyword""", mapOf("keyword" to keyword))
}