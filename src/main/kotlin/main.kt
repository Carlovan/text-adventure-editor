import model.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.launch

fun main(args: Array<String>) {
    ExposedExecutor.submit {
        Database.connect(
            url = "jdbc:postgresql://cauldron.liquoricemage.it:5432/textadveditor_db",
            driver = "org.postgresql.Driver",
            user = "textadveditor_u",
            password = "gaheANeHTMOYDd2tfko7HGmN",
            manager = { ThreadLocalTransactionManager(it, 0) }
        )

        transaction {
            addLogger(StdOutSqlLogger)
//            SchemaUtils.drop(Steps, Choices, DiceConstraints,
//                Skills, PlayerConfigurations, SkillsPlayerConfigurations, SkillConstraints,
//                Statistics, StatisticContraints, StatisticsSkills, Enemies, EnemiesStatistics,
//                Items, ItemSlots, Loots, LootsItems, PlayerAvailableSlots, EnemiesSteps, StatisticsItems,
//                ItemConstraints, ItemSkillActivations, StatisticsPlayerConfigurations)

            SchemaUtils.create(
                Adventures, Steps, Choices, DiceConstraints,
                Skills, PlayerConfigurations, SkillsPlayerConfigurations, SkillConstraints,
                Statistics, StatisticConstraints, StatisticsSkills, Enemies, EnemiesStatistics,
                Items, ItemSlots, Loots, LootsItems, PlayerAvailableSlots, EnemiesSteps, StatisticsItems,
                ItemConstraints, ItemSkillActivations, StatisticsPlayerConfigurations
            )
        }
    }

    launch<TextAdventureEditorApp>(args)
}