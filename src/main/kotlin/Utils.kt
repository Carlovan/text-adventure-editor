import java.util.*

fun <T> Optional<T>.peek(op: (T) -> Unit) = this.apply { ifPresent(op) }
fun <T> Optional<T>.onEmpty(op: () -> Unit) = this.apply { if (!isPresent) op() }