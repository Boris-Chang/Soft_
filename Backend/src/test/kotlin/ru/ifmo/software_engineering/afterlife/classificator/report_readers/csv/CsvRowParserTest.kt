package ru.ifmo.software_engineering.afterlife.classificator.report_readers.csv

import arrow.core.Validated
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Test
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessKind
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinKind
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class CsvRowParserTest {
    @Test
    fun `When parser is for Goodness Evidence it Should parse row correctly`() {
        val row = emptyList<String>()
        val rowNum = 1
        val now = ZonedDateTime.now()

        val dateTimeColumn: CsvColumn<ZonedDateTime> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Valid(now)
        }
        val kindColumn: CsvColumn<GoodnessKind> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Valid(GoodnessKind.AMBITION)
        }

        val result = GoodnessCsvRowParser(dateTimeColumn, kindColumn).parseRow(row, rowNum)
        val expectedValue = GoodnessEvidence(0, GoodnessKind.AMBITION, now)

        assertEquals(Validated.Valid(expectedValue), result)
    }

    @Test
    fun `When parser is for Goodness Evidence and date cell failed should return error`() {
        val row = emptyList<String>()
        val rowNum = 1
        val expectedError = CsvParseException("", 0, 0)

        val dateTimeColumn: CsvColumn<ZonedDateTime> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Invalid(expectedError)
        }
        val kindColumn: CsvColumn<GoodnessKind> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Valid(GoodnessKind.AMBITION)
        }

        val result = GoodnessCsvRowParser(dateTimeColumn, kindColumn).parseRow(row, rowNum)

        assertEquals(Validated.Invalid(expectedError), result)
    }

    @Test
    fun `When parser is for Goodness Evidence and kind cell failed should return error`() {
        val row = emptyList<String>()
        val rowNum = 1
        val now = ZonedDateTime.now()
        val expectedError = CsvParseException("", 0, 0)

        val dateTimeColumn: CsvColumn<ZonedDateTime> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Valid(now)
        }
        val kindColumn: CsvColumn<GoodnessKind> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Invalid(expectedError)
        }

        val result = GoodnessCsvRowParser(dateTimeColumn, kindColumn).parseRow(row, rowNum)

        assertEquals(Validated.Invalid(expectedError), result)
    }

    @Test
    fun `When parser is for Sin Evidence it Should parse row correctly`() {
        val row = emptyList<String>()
        val rowNum = 1
        val now = ZonedDateTime.now()

        val dateTimeColumn: CsvColumn<ZonedDateTime> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Valid(now)
        }
        val kindColumn: CsvColumn<SinKind> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Valid(SinKind.VIOLENCE)
        }
        val atonedDateColumn: CsvColumn<ZonedDateTime?> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Valid(now)
        }

        val result = SinCsvRowParser(dateTimeColumn, kindColumn, atonedDateColumn).parseRow(row, rowNum)
        val expectedValue = SinEvidence(0, SinKind.VIOLENCE, now, now)

        assertEquals(Validated.Valid(expectedValue), result)
    }

    @Test
    fun `When parser is for Sin Evidence and date cell failed should return error`() {
        val row = emptyList<String>()
        val rowNum = 1
        val now = ZonedDateTime.now()
        val expectedError = CsvParseException("", 0, 0)

        val dateTimeColumn: CsvColumn<ZonedDateTime> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Invalid(expectedError)
        }
        val kindColumn: CsvColumn<SinKind> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Valid(SinKind.VIOLENCE)
        }
        val atonedDateColumn: CsvColumn<ZonedDateTime?> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Valid(now)
        }

        val result = SinCsvRowParser(dateTimeColumn, kindColumn, atonedDateColumn).parseRow(row, rowNum)

        assertEquals(Validated.Invalid(expectedError), result)
    }

    @Test
    fun `When parser is for Sin Evidence and kind cell failed should return error`() {
        val row = emptyList<String>()
        val rowNum = 1
        val now = ZonedDateTime.now()
        val expectedError = CsvParseException("", 0, 0)

        val dateTimeColumn: CsvColumn<ZonedDateTime> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Invalid(expectedError)
        }
        val kindColumn: CsvColumn<SinKind> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Invalid(expectedError)
        }
        val atonedDateColumn: CsvColumn<ZonedDateTime?> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Valid(now)
        }

        val result = SinCsvRowParser(dateTimeColumn, kindColumn, atonedDateColumn).parseRow(row, rowNum)

        assertEquals(Validated.Invalid(expectedError), result)
    }

    @Test
    fun `When parser is for Sin Evidence and attonedAt cell failed should return error`() {
        val row = emptyList<String>()
        val rowNum = 1
        val expectedError = CsvParseException("", 0, 0)

        val dateTimeColumn: CsvColumn<ZonedDateTime> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Invalid(expectedError)
        }
        val kindColumn: CsvColumn<SinKind> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Valid(SinKind.VIOLENCE)
        }
        val atonedDateColumn: CsvColumn<ZonedDateTime?> = mock {
            on {
                parseCell(row, rowNum)
            } doReturn Validated.Invalid(expectedError)
        }

        val result = SinCsvRowParser(dateTimeColumn, kindColumn, atonedDateColumn).parseRow(row, rowNum)

        assertEquals(Validated.Invalid(expectedError), result)
    }
}
