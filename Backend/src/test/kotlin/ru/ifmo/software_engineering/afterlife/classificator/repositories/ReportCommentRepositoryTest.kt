package ru.ifmo.software_engineering.afterlife.classificator.repositories

import arrow.core.Validated
import arrow.core.getOrElse
import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.RandomStringUtils
import org.jooq.DSLContext
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.springframework.boot.test.context.SpringBootTest
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportComment
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.database.Tables.SOUL_REPORT_COMMENT
import ru.ifmo.software_engineering.afterlife.utils.spek_spring.createContext
import ru.ifmo.software_engineering.afterlife.utils.toUtc
import ru.ifmo.software_engineering.afterlife.utils.truncatedToSeconds
import java.time.ZonedDateTime
import kotlin.test.assertEquals

@SpringBootTest
object ReportCommentRepositoryTest : Spek({
    lateinit var dsl: DSLContext
    lateinit var soulRepository: SoulRepository
    lateinit var repository: ReportCommentRepository

    beforeEachGroup {
        val ctx = createContext(ReportCommentRepositoryTest::class)
        dsl = ctx.inject()
        soulRepository = ctx.inject()
        repository = ctx.inject()
    }

    fun createTestSoul(): Soul {
        val soul = Soul(0, RandomStringUtils.random(10), RandomStringUtils.random(10), ZonedDateTime.now(), null)
        return runBlocking { soulRepository.insertOne(soul) }
    }

    Feature("findCommentsBySoul") {
        fun addCommentToSoul(soul: Soul): ReportComment {
            return dsl.insertInto(SOUL_REPORT_COMMENT)
                    .set(SOUL_REPORT_COMMENT.SOUL_ID, soul.id)
                    .set(SOUL_REPORT_COMMENT.COMMENT_TEXT, RandomStringUtils.random(10))
                    .set(SOUL_REPORT_COMMENT.CREATED_AT, ZonedDateTime.now().toOffsetDateTime())
                    .returning()
                    .fetch()
                    .map { ReportComment(it[SOUL_REPORT_COMMENT.ID], it[SOUL_REPORT_COMMENT.COMMENT_TEXT],
                            it[SOUL_REPORT_COMMENT.CREATED_AT].toZonedDateTime()) }
                    .first()!!
        }

        lateinit var result: Validated<Throwable, List<ReportComment>>
        fun findCommentsBySoul(soul: Soul) {
            result = Validated.catch {
                runBlocking {
                    repository.findCommentsBySoul(soul)
                }
            }
        }

        Scenario("Soul not exist") {
            lateinit var notExistingSoul: Soul
            Given("Soul not exist") {
                notExistingSoul = Soul(1, "", "", ZonedDateTime.now(), null)
            }

            When("Finding comments by soul") {
                findCommentsBySoul(notExistingSoul)
            }

            Then("Result should not have an exception") {
                assert(result.isValid)
            }

            And("Result should have an empty list of comments") {
                val expected = listOf<ReportComment>()
                assertEquals(expected, result.getOrElse { null })
            }
        }

        Scenario("Soul exists but no comments there") {
            lateinit var existingSoul: Soul
            Given("Soul exists") {
                existingSoul = createTestSoul()
            }

            When("Finding comments by soul") {
                findCommentsBySoul(existingSoul)
            }

            Then("Result should not have an exception") {
                assert(result.isValid)
            }

            And("Result should have an empty list of comments") {
                val expected = listOf<ReportComment>()
                assertEquals(expected, result.getOrElse { null })
            }
        }

        Scenario("Soul exists and has few comments") {
            lateinit var existingSoul: Soul
            Given("Soul exists") {
                existingSoul = createTestSoul()
            }

            lateinit var createdComments: List<ReportComment>
            And("It has few comments") {
                val comment1 = addCommentToSoul(existingSoul)
                val comment2 = addCommentToSoul(existingSoul)
                createdComments = listOf(comment2, comment1)
            }

            When("Finding comments by soul") {
                findCommentsBySoul(existingSoul)
            }

            Then("Result should not have an exception") {
                assert(result.isValid)
            }

            And("Result should have an list of created comments") {
                assertEquals(createdComments, result.getOrElse { null })
            }
        }

        Scenario("Few Souls exist and each has few comments") {
            lateinit var existingSoul1: Soul
            lateinit var existingSoul2: Soul
            Given("2 Souls exist") {
                existingSoul1 = createTestSoul()
                existingSoul2 = createTestSoul()
            }

            lateinit var createdCommentsOfSoul1: List<ReportComment>
            lateinit var createdCommentsOfSoul2: List<ReportComment>
            And("Each have 2 comments") {
                val comment1Soul1 = addCommentToSoul(existingSoul1)
                val comment2Soul1 = addCommentToSoul(existingSoul1)
                createdCommentsOfSoul1 = listOf(comment2Soul1, comment1Soul1)

                val comment1Soul2 = addCommentToSoul(existingSoul2)
                val comment2Soul2 = addCommentToSoul(existingSoul2)
                createdCommentsOfSoul2 = listOf(comment2Soul2, comment1Soul2)
            }

            lateinit var actualCommentsOfSoul1: List<ReportComment>
            lateinit var actualCommentsOfSoul2: List<ReportComment>
            When("Finding comments by soul") {
                findCommentsBySoul(existingSoul1)
                actualCommentsOfSoul1 = result.getOrElse { null }!!
                findCommentsBySoul(existingSoul2)
                actualCommentsOfSoul2 = result.getOrElse { null }!!
            }

            Then("Result of first soul should have an list of its comments") {
                assertEquals(createdCommentsOfSoul1, actualCommentsOfSoul1)
            }

            Then("Result of second soul should have an list of its comments") {
                assertEquals(createdCommentsOfSoul2, actualCommentsOfSoul2)
            }
        }
    }

    Feature("save") {
        lateinit var result: ReportComment
        fun saveCommentForSoul(soul: Soul, comment: ReportComment) {
            result = runBlocking { repository.save(soul, comment) }
        }

        Scenario("Saving comment for existing soul") {
            lateinit var existingSoul: Soul
            Given("Soul exists") {
                existingSoul = createTestSoul()
            }

            val commentToInsert = ReportComment(0, RandomStringUtils.random(10), ZonedDateTime.now())
            When("Saving comment for soul") {
                saveCommentForSoul(existingSoul, commentToInsert)
            }

            Then("Result has correct created comment") {
                assertEquals(result.text, commentToInsert.text)
                assertEquals(
                        result.createdAt.truncatedToSeconds().toUtc(),
                        commentToInsert.createdAt.truncatedToSeconds().toUtc())
            }

            And("Repository get comment returns same comment") {
                val actualComments = runBlocking { repository.findCommentsBySoul(existingSoul) }
                val expectedComments = listOf(result)
                assertEquals(expectedComments, actualComments)
            }
        }
    }
})