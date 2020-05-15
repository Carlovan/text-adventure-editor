package sqlutils

class PSQLError(val error: PSQLState): Throwable()

fun <T> safeTransaction(): Result<T> {
    TODO()
}
