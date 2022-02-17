package ru.ifmo.software_engineering.afterlife.classificator.repositories

import kotlinx.coroutines.future.await
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import ru.ifmo.software_engineering.afterlife.classificator.domain.AfterworldSection
import ru.ifmo.software_engineering.afterlife.classificator.domain.ArguedClassifierDecision
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.Tables.ARGUED_CLASSIFIER_DECISION
import ru.ifmo.software_engineering.afterlife.database.enums.ArguedByKind
import ru.ifmo.software_engineering.afterlife.database.tables.records.ArguedClassifierDecisionRecord

interface ArguedClassifierDecisionRepository {
    suspend fun findArgueForSoul(soul: Soul): ArguedClassifierDecision
    suspend fun updateSoulArgue(soul: Soul, argue: ArguedClassifierDecision): ArguedClassifierDecision
}

@Repository
class ArguedClassifierDecisionRepositoryImpl(
        private val dsl: DSLContext,
        private val argueToSectionMapper: RecordMapper<ArguedClassifierDecisionRecord, AfterworldSection>,
        private val sectionToArgueUnmapper: RecordUnmapper<AfterworldSection, ArguedClassifierDecisionRecord>
): ArguedClassifierDecisionRepository {
    override suspend fun findArgueForSoul(soul: Soul): ArguedClassifierDecision {
        val argues = dsl.select().from(ARGUED_CLASSIFIER_DECISION)
                .where(ARGUED_CLASSIFIER_DECISION.SOUL_ID.eq(soul.id))
                .fetchAsync()
                .await()
                .map { it.into(ARGUED_CLASSIFIER_DECISION) }

        val godArgueRecord = argues.firstOrNull { it.arguedBy == ArguedByKind.GOD }
        val godsArgue = godArgueRecord?.let {
            argueToSectionMapper.map(it)
        }
        val devilArgueRecord = argues.firstOrNull { it.arguedBy == ArguedByKind.DEVIL }
        val devilsArgue = devilArgueRecord?.let {
            argueToSectionMapper.map(it)
        }

        return ArguedClassifierDecision(godsArgue, devilsArgue, godArgueRecord != null, devilArgueRecord != null)
    }

    override suspend fun updateSoulArgue(soul:Soul, argue: ArguedClassifierDecision): ArguedClassifierDecision {
        val godsArgueRecord = if (argue.isArguedByGod) {
            sectionToArgueUnmapper.unmap(argue.godChangedDecision).apply {
                soulId = soul.id
                arguedBy = ArguedByKind.GOD
            }
        } else null
        val devilsArgueRecord = if (argue.isArguedByDevil) {
            sectionToArgueUnmapper.unmap(argue.devilChangedDecision).apply {
                soulId = soul.id
                arguedBy = ArguedByKind.DEVIL
            }
        } else null

        val recordsToInsert = listOf(godsArgueRecord, devilsArgueRecord).filterNotNull()

        this.dsl.insertInto(ARGUED_CLASSIFIER_DECISION, *ARGUED_CLASSIFIER_DECISION.fields())
                .valuesOfRecords(recordsToInsert)
                .onConflict(ARGUED_CLASSIFIER_DECISION.SOUL_ID, ARGUED_CLASSIFIER_DECISION.ARGUED_BY)
                .doUpdate()
                .set(ARGUED_CLASSIFIER_DECISION.AFTERWORLD_KIND, DSL.field(
                        "EXCLUDED." + ARGUED_CLASSIFIER_DECISION.AFTERWORLD_KIND.unqualifiedName,
                        ARGUED_CLASSIFIER_DECISION.AFTERWORLD_KIND.type))
                .set(ARGUED_CLASSIFIER_DECISION.SECTION_NUMBER, DSL.field(
                        "EXCLUDED." + ARGUED_CLASSIFIER_DECISION.SECTION_NUMBER.unqualifiedName,
                        ARGUED_CLASSIFIER_DECISION.SECTION_NUMBER.type))
                .executeAsync()
                .await()

        return this.findArgueForSoul(soul)
    }

}