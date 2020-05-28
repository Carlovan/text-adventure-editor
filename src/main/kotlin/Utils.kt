import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun <T> Optional<T>.peek(op: (T) -> Unit) = this.apply { ifPresent(op) }
fun <T> Optional<T>.onEmpty(op: () -> Unit) = this.apply { if (!isPresent) op() }

val ExposedExecutor: ExecutorService = Executors.newSingleThreadExecutor()

fun String.ellipses(maxLength: Int) =
    if (this.length > maxLength - 3) {
        this.substring(0, maxLength - 3) + "..."
    } else {
        this
    }

fun Int.toStringWithSign() = DecimalFormat("+#;-#").format(this)