package com.xap4o.kwony.db

import com.xap4o.kwony.processing.AnalyzeResult

interface AnalyzeResultDb {
    fun persist(r: AnalyzeResult): Int
}

class PostgresAnalyzeResultDb(val db: Db) : AnalyzeResultDb {
    override fun persist(r: AnalyzeResult): Int =
            db.insert("""INSERT INTO analyze_result(total, positive, negative, errors, duration)
                         VALUES (:total, :positive, :negative, :errors, :duration)""",
                    mapOf("total" to r.total, "positive" to r.positive, "negative" to r.negative,
                            "errors" to r.errors, "duration" to r.duration),
                    { it.int("id") })
}