import java.util.*

fun <T> Optional<T>.peek(op: (T) -> Unit) = this.apply { ifPresent(op) }
fun <T> Optional<T>.onEmpty(op: () -> Unit) = this.apply { if (!isPresent) op() }

fun String.ellipses(maxLength: Int) =
    if (this.length > maxLength - 3) {
        this.substring(0, maxLength - 3) + "..."
    } else {
        this
    }