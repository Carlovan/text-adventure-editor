package controller

import model.PlayerAvailableSlots
import model.PlayerConfiguration
import model.PlayerConfigurations
import model.StatisticsPlayerConfigurations
import model.SkillsPlayerConfigurations
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.MaybePSQLError
import sqlutils.safeTransaction
import tornadofx.asObservable
import viewmodel.*

class PlayerConfigurationController : ControllerWithContextAdventure() {
    val playerConfigurations
        get() = transaction {
            PlayerConfiguration.find { PlayerConfigurations.adventure eq contextAdventure!!.item.id }
                .map { MasterPlayerConfigurationViewModel(it) }
                .toList().asObservable()
        }

    fun createConfiguration(configuration: SimplePlayerConfigurationViewModel) = safeTransaction {
        PlayerConfiguration.new {
            adventure = contextAdventure!!.item.id
            fromViewModel(configuration)
        }
    }

    fun deleteConfiguration(configuration: SimplePlayerConfigurationViewModel) = safeTransaction {
        configuration.item.delete()
    }

    fun commit(changes: Sequence<SimplePlayerConfigurationViewModel>) = safeTransaction {
        changes.forEach { it.saveData() }
    }

    fun commit(change: SimplePlayerConfigurationViewModel) =
        commit(sequenceOf(change))

    fun removeSlot(configuration: SimplePlayerConfigurationViewModel, slot: PlayerAvailableSlotViewModel) =
        safeTransaction {
            PlayerAvailableSlots.deleteWhere {
                (PlayerAvailableSlots.playerConf eq configuration.item.id) and
                        (PlayerAvailableSlots.itemSlot eq slot.itemSlot.value.item.id) and
                        (PlayerAvailableSlots.name eq slot.name.value)
            }
        }

    fun addSlot(configuration: SimplePlayerConfigurationViewModel, slot: PlayerAvailableSlotViewModel) =
        safeTransaction {
            PlayerAvailableSlots.insert {
                it[itemSlot] = slot.itemSlot.value.item.id
                it[playerConf] = configuration.item.id
                it[name] = slot.name.value
            }
        }

    fun getDetail(configuration: MasterPlayerConfigurationViewModel) =
        transaction { DetailPlayerConfigurationViewModel(configuration.item) }

    fun removeStatistic(
        configuration: DetailPlayerConfigurationViewModel,
        statisticViewModel: StatisticViewModel?
    ): MaybePSQLError {
        return safeTransaction {
            StatisticsPlayerConfigurations.deleteWhere {
                StatisticsPlayerConfigurations.playerConf eq configuration.item.id and (StatisticsPlayerConfigurations.statistic eq statisticViewModel!!.item.id)
            }
        }
    }

    fun addStatistic(configuration: DetailPlayerConfigurationViewModel, statistic: StatisticViewModel): MaybePSQLError {
        return safeTransaction {
            StatisticsPlayerConfigurations.insert {
                it[StatisticsPlayerConfigurations.playerConf] = configuration.item.id
                it[StatisticsPlayerConfigurations.statistic] = statistic.item.id
            }
        }
    }

    fun removeSkill(configuration: SimplePlayerConfigurationViewModel, skillValue: SkillViewModel) = safeTransaction {
        SkillsPlayerConfigurations.deleteWhere {
            (SkillsPlayerConfigurations.playerConf eq configuration.item.id) and
                    (SkillsPlayerConfigurations.skill eq skillValue.item.id)
        }
    }

    fun addSkill(configuration: SimplePlayerConfigurationViewModel, skillValue: SkillViewModel) = safeTransaction {
        SkillsPlayerConfigurations.insert {
            it[playerConf] = configuration.item.id
            it[skill] = skillValue.item.id
        }
    }
}