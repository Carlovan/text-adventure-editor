package controller

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import tornadofx.Controller
import java.sql.Connection

abstract class AbstractController : Controller() {
    init {
        Database.connect(url = "jdbc:postgresql://cauldron.liquoricemage.it:5432/textadveditor_db",
                         driver = "org.postgresql.Driver",
                         user = "textadveditor_u",
                         password = "gaheANeHTMOYDd2tfko7HGmN")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }
}