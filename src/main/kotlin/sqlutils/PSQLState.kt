package sqlutils

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.postgresql.util.PSQLException

fun ExposedSQLException.toPSQLState() = this.filterIsInstance(PSQLException::class.java).firstOrNull()?.sqlState?.let { code ->
    PSQLState.values().find { it.code == code }
} ?: PSQLState.UNKNOWN