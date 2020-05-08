import model.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import java.sql.Connection
import java.sql.DriverManager
import java.util.*


fun main(args: Array<String>) {
    Database.connect(url = "jdbc:postgresql://cauldron.liquoricemage.it:5432/textadveditor_db",
                     driver = "org.postgresql.Driver",
                     user = "textadveditor_u",
                     password = "gaheANeHTMOYDd2tfko7HGmN")

    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Adventures, Steps, Choices, DiceConstraint)
    }

    launch<TextAdventureEditorApp>(args)
}