package sqlutils

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

typealias MaybePSQLError = Optional<PSQLState>

fun safeTransaction(statement: Transaction.() -> Unit): MaybePSQLError {
    try {
        println("SafeTransaction in ${Thread.currentThread().name}")
        transaction {
            println("transaction in ${Thread.currentThread().name}")
            statement()
        }
    } catch (exception: ExposedSQLException) {
        return Optional.of(exception.toPSQLState())
    }
    return Optional.empty()
}
