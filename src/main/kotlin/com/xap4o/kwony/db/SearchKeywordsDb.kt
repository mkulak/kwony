package com.xap4o.kwony.db

import com.xap4o.kwony.twitter.Keyword
import com.xap4o.kwony.utils.Try

interface SearchKeywordsDb {
    fun getAll(): Try<List<Keyword>>
    fun persist(keyword: Keyword): Try<Int>
    fun delete(keyword: Keyword): Try<Int>
}


class PostgresSearchKeywordsDb(val db: Db) : SearchKeywordsDb {
    override fun getAll(): Try<List<Keyword>> =
            db.select("""SELECT value FROM search_keyword ORDER BY id""",
                    mapper = { row -> Keyword(row.string("value")) })

    override fun persist(keyword: Keyword): Try<Int> =
            db.insert("""INSERT INTO search_keyword(value) VALUES (:keyword)""",
                    mapOf("keyword" to keyword.value),
                    f = { it.int("id") })

    override fun delete(keyword: Keyword): Try<Int> =
            db.update("""DELETE FROM search_keyword WHERE value=:keyword""", mapOf("keyword" to keyword.value))
}