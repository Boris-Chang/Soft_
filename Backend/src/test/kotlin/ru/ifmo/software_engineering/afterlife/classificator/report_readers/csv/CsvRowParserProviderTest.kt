package ru.ifmo.software_engineering.afterlife.classificator.report_readers.csv

import arrow.core.valueOr
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class CsvRowParserProviderTest {
    @Test
    fun `When providing parser for GoodnessEvidence should create header if every column exist in header`() {
        val header = listOf("date", "kind")
        val missingKindHeader = listOf("date")
        val missingDateHeader = listOf("kind")
        val wrongNamesHeader = listOf("kind", "dates")
        val emptyHeader = emptyList<String>()

        val provider = GoodnessCsvRowParserProvider(
            GoodnessCsvRowParserProvider.GoodnessCsvHeaderNames(header[1], header[0]))

        val successfulParser = provider.provideRowParser(header)
        val missingKindFailure = provider.provideRowParser(missingKindHeader)
        val missingDateFailed = provider.provideRowParser(missingDateHeader)
        val wrongDateFailed = provider.provideRowParser(wrongNamesHeader)
        val emptyHeaderFailure = provider.provideRowParser(emptyHeader)

        assertTrue(successfulParser.isValid)
        assertTrue(missingKindFailure.isInvalid)
        assertTrue(missingDateFailed.isInvalid)
        assertTrue(wrongDateFailed.isInvalid)
        assertTrue(emptyHeaderFailure.isInvalid)
        assertTrue {
            when (val parser = successfulParser.valueOr { null!! }) {
                is GoodnessCsvRowParser ->
                    parser.goodnessKindColumn.index == 1 && parser.dateColumn.index == 0
                else -> false
            }
        }
    }

    @Test
    fun `When providing parser for SinEvidence should create header if every column exist in header`() {
        val header = listOf("date", "kind", "atoned")
        val missingKindHeader = listOf("date", "atoned")
        val missingDateHeader = listOf("kind", "atoned")
        val missingAtonedHeader = listOf("kind", "date")
        val wrongNamesHeader = listOf("kind", "dates", "atoned")
        val emptyHeader = emptyList<String>()

        val provider = SinCsvRowParserProvider(
            SinCsvRowParserProvider.SinCsvHeaderNames(header[0], header[1], header[2]))

        val successfulParser = provider.provideRowParser(header)
        val missingKindFailure = provider.provideRowParser(missingKindHeader)
        val missingDateFailed = provider.provideRowParser(missingDateHeader)
        val missingAtonedFailed = provider.provideRowParser(missingAtonedHeader)
        val wrongDateFailed = provider.provideRowParser(wrongNamesHeader)
        val emptyHeaderFailure = provider.provideRowParser(emptyHeader)

        assertTrue(successfulParser.isValid)
        assertTrue(missingKindFailure.isInvalid)
        assertTrue(missingDateFailed.isInvalid)
        assertTrue(missingAtonedFailed.isInvalid)
        assertTrue(wrongDateFailed.isInvalid)
        assertTrue(emptyHeaderFailure.isInvalid)
        assertTrue {
            when (val parser = successfulParser.valueOr { null!! }) {
                is SinCsvRowParser ->
                    parser.kindColumn.index == 1 &&
                        parser.dateColumn.index == 0 &&
                        parser.atonedAtColumn.index == 2
                else -> false
            }
        }
    }

}