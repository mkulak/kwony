package com.xap4o.kwony.db

interface SearchKeywordsDb {
    fun getAll(): List<String>
    fun persist(keyword: String): Int
    fun delete(keyword: String): Int
}


class PostgresSearchKeywordsDb(val db: Db) : SearchKeywordsDb {
    override fun getAll(): List<String> =
            db.select("""SELECT value FROM search_keyword ORDER BY id""", mapper = { row -> row.string("value") })

    override fun persist(keyword: String): Int =
            db.insert("""INSERT INTO search_keyword(value) VALUES (:keyword)""",
                    mapOf("keyword" to keyword),
                    f = { it.int("id") })

    override fun delete(keyword: String): Int =
            db.update("""DELETE FROM search_keyword WHERE value=:keyword""", mapOf("keyword" to keyword))
}