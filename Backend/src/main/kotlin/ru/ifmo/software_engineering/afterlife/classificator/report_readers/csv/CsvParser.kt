package ru.ifmo.software_engineering.afterlife.classificator.report_readers.csv

import arrow.core.Validated
import arrow.core.computations.either
import arrow.core.traverseEither
import com.opencsv.CSVReader
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinEvidence
import java.io.Reader

interface CsvParser<T> {
    fun parseCsv(reader: Reader): Validated<CsvParseException, List<T>>
}

sealed class CsvParserImpl<T>(
    private val rowParserProvider: CsvRowParserProvider<T>
) : CsvParser<T> {
    override fun parseCsv(reader: Reader): Validated<CsvParseException, List<T>> =
        this.rowParserProvider.parseRows(reader)

    private fun <T> CsvRowParserProvider<T>.parseRows(reader: Reader): Validated<CsvParseException, List<T>> {
        val csvReader = CSVReader(reader)

        val headerRow = csvReader.readNext()
            ?: return Validated.Invalid(CsvParseException("No header found", 0, 0))

        val rows = csvReader.readAll()

        return either.eager<CsvParseException, List<T>> {
            val parser = provideRowParser(headerRow.toList()).bind()
            parser.parseRows(rows).bind()
        }.toValidated()
    }

    private fun <T> CsvRowParser<T>.parseRows(rows: Iterable<Array<String>>): Validated<CsvParseException, List<T>> {
        // Add header row number to number of row
        val offset = 1

        return rows.withIndex().traverseEither { (i, row) ->
            parseRow(row.toList(), i + offset).toEither()
        }.toValidated()
    }
}

class GoodnessEvidencesCsvParser(rowParserProvider: CsvRowParserProvider<GoodnessEvidence>) :
    CsvParserImpl<GoodnessEvidence>(rowParserProvider)

class SinEvidencesCsvParser(rowParserProvider: CsvRowParserProvider<SinEvidence>) :
    CsvParserImpl<SinEvidence>(rowParserProvider)
