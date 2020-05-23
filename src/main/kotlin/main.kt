import kotlinx.coroutines.*
import model.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val ExposedExecutor: ExecutorService = Executors.newSingleThreadExecutor()

fun main(args: Array<String>) {
    ExposedScope.launch {
        Database.connect(
            url = "jdbc:postgresql://cauldron.liquoricemage.it:5432/textadveditor_db",
            driver = "org.postgresql.Driver",
            user = "textadveditor_u",
            password = "gaheANeHTMOYDd2tfko7HGmN"
        )
        println(Thread.currentThread().name)

        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(
                Adventures, Steps, Choices, DiceConstraints,
                Skills, PlayerConfigurations, SkillsPlayerConfigurations, SkillConstraints,
                Statistics, StatisticContraints, StatisticsSkills, Enemies, EnemiesStatistics,
                Items, ItemSlots, Loots, LootsItems, PlayerAvailableSlots, EnemiesSteps, StatisticsItems,
                ItemConstraints, ItemSkillActivations, StatisticsPlayerConfigurations
            )
        }
    }

    launch<TextAdventureEditorApp>(args)
}