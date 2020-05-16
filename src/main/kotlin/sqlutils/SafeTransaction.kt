package sqlutils

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

typealias MaybePSQLError = Optional<PSQLState>

fun safeTransaction(statement: Transaction.() -> Unit): MaybePSQLError {
    try {
        transaction(statement = statement)
    } catch (exception: ExposedSQLException) {
        return Optional.of(exception.toPSQLState())
    }
    return Optional.empty()
}
