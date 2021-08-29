package ru.ifmo.software_engineering.afterlife.classificator.report_readers.csv

import arrow.core.Validated
import arrow.core.computations.either
import arrow.core.computations.nullable
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessKind
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinKind
import java.util.*

interface CsvRowParser<T> {
    val columns: List<CsvColumn<*>>
    fun parseRow(row: List<String>, rowNum: Int): Validated<CsvParseException, T>
}

interface CsvRowParserProvider<T> {
    fun provideRowParser(header: List<String>): Validated<CsvParseException, CsvRowParser<T>>
}

class GoodnessCsvRowParserProvider(
    private val headerNames: GoodnessCsvHeaderNames,
) : CsvRowParserProvider<GoodnessEvidence> {
    data class GoodnessCsvHeaderNames(
        val kindColumn: String,
        val dateColumn: String,
    )

    private fun getDateColumn(header: List<String>): CsvColumn<Date>? {
        val headerIndex = header.indexOf(this.headerNames.dateColumn)
        return if (headerIndex < 0 )  null
        else DateCsvColumn(
            headerIndex,
            this.headerNames.dateColumn)
    }

    private fun getGoodnessKindColumn(header: List<String>): CsvColumn<GoodnessKind>? {
        val headerIndex = header.indexOf(this.headerNames.kindColumn)
        return if (headerIndex < 0 )  null
        else EnumCsvColumn(
            headerIndex,
            this.headerNames.kindColumn,
            GoodnessKind::class)
    }

    override fun provideRowParser(header: List<String>): Validated<CsvParseException, CsvRowParser<GoodnessEvidence>> {
        val dateCol = this.getDateColumn(header)
            ?: return Validated.Invalid(CsvParseException("Date column not provided", 1, -1))
        val goodnessCol = this.getGoodnessKindColumn(header)
            ?: return Validated.Invalid(CsvParseException("Goodness Kind column not provided", 1, -1))
        return Validated.Valid(GoodnessCsvRowParser(dateCol, goodnessCol))
    }
}

class SinCsvRowParserProvider(private val headerNames: SinCsvHeaderNames) : CsvRowParserProvider<SinEvidence> {
    data class SinCsvHeaderNames(
        val dateColumn: String,
        val sinKindColumn: String,
        val atonedAtColumn: String?,
    )

    override fun provideRowParser(header: List<String>): Validated<CsvParseException, CsvRowParser<SinEvidence>> = nullable.eager {
        val dateCol = getDateColumn(header).bind()
        val kindCol = getKindColumn(header).bind()
        val atonedAtCol = getAtonedAtColumn(header).bind()
        Validated.Valid(SinCsvRowParser(dateCol, kindCol, atonedAtCol))
    } ?: Validated.Invalid(CsvParseException("Not all headers provided", 0, 0))

    private fun getDateColumn(header: List<String>): CsvColumn<Date>? {
        val headerIndex = header.indexOf(this.headerNames.dateColumn)

        return if (headerIndex >= 0)
            DateCsvColumn(headerIndex, header[headerIndex])
        else null
    }

    private fun getKindColumn(header: List<String>): CsvColumn<SinKind>? {
        val headerIndex = header.indexOf(this.headerNames.dateColumn)

        return if (headerIndex >= 0)
            EnumCsvColumn(headerIndex, header[headerIndex], SinKind::class)
        else null
    }

    private fun getAtonedAtColumn(header: List<String>): CsvColumn<Date?>? {
        val headerIndex = header.indexOf(this.headerNames.dateColumn)

        return if (headerIndex >= 0)
            OptionalColumn(DateCsvColumn(headerIndex, header[headerIndex]))
        else null
    }
}

class GoodnessCsvRowParser(
    private val dateColumn : CsvColumn<Date>,
    private val goodnessKindColumn: CsvColumn<GoodnessKind>
) : CsvRowParser<GoodnessEvidence> {
    override val columns: List<CsvColumn<*>>
        get() = listOf(goodnessKindColumn, dateColumn)

    override fun parseRow(row: List<String>, rowNum: Int): Validated<CsvParseException, GoodnessEvidence> =
        either.eager<CsvParseException, GoodnessEvidence> {
            val kind = goodnessKindColumn.parseCell(row, rowNum).bind()
            val date = dateColumn.parseCell(row, rowNum).bind()
            GoodnessEvidence(0, kind, date)
        }.toValidated()
}

class SinCsvRowParser(
    private val dateColumn: CsvColumn<Date>,
    private val kindColumn: CsvColumn<SinKind>,
    private val atonedAtColumn: CsvColumn<Date?>
) : CsvRowParser<SinEvidence> {
    override val columns: List<CsvColumn<*>> = listOf(dateColumn, kindColumn, atonedAtColumn)

    override fun parseRow(row: List<String>, rowNum: Int): Validated<CsvParseException, SinEvidence> =
        either.eager<CsvParseException, SinEvidence> {
            val date = dateColumn.parseCell(row, rowNum).bind()
            val kind = kindColumn.parseCell(row, rowNum).bind()
            val atonedAt = atonedAtColumn.parseCell(row, rowNum).bind()

            SinEvidence(0, kind, date, atonedAt)
        }.toValidated()
}