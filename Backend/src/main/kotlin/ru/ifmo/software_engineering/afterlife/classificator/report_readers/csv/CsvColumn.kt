package ru.ifmo.software_engineering.afterlife.classificator.report_readers.csv

import arrow.core.Validated
import ru.ifmo.software_engineering.afterlife.utils.enumValueOrNull
import ru.ifmo.software_engineering.afterlife.utils.tryParseDateRfc3339
import java.util.*
import kotlin.reflect.KClass

interface CsvColumn<T> {
    val index: Int
    val name: String

    fun parseCell(row: List<String>, rowNum: Int): Validated<CsvParseException, T>
    fun getRawCellValue(row: List<String>): String? =
        when {
            this.index < 0 -> null
            this.index >= row.size -> null
            else -> row[this.index]
        }
}

class DateCsvColumn(override val index: Int, override val name: String): CsvColumn<Date> {
    override fun parseCell(row: List<String>, rowNum: Int): Validated<CsvParseException, Date> {
        val rawCellValue = getRawCellValue(row)
            ?: return Validated.Invalid(
                CsvParseException("Value for \"$name\" column not provided", rowNum, this.index))
        return rawCellValue.tryParseDateRfc3339()
            ?.let {
                Validated.Valid(it)
            } ?: Validated.Invalid(CsvParseException("Date parse exception", rowNum, this.index))
    }
}

class EnumCsvColumn<E : Enum<*>>(
    override val index: Int,
    override val name: String,
    private val kClass: KClass<E>
) : CsvColumn<E> {
    override fun parseCell(row: List<String>, rowNum: Int): Validated<CsvParseException, E> {
        val rawValue = this.getRawCellValue(row)
            ?: return Validated.Invalid(CsvParseException("Value for \"$name\" column not provided", rowNum, this.index))
        return enumValueOrNull(rawValue, kClass)
            ?.let {
                Validated.Valid(it)
            } ?: Validated.Invalid(CsvParseException("Unknown value for \"$name\" column ", rowNum, this.index))
    }
}

class OptionalColumn<T>(private val column: CsvColumn<T>) : CsvColumn<T?> {
    override val index: Int = column.index
    override val name: String = column.name

    override fun parseCell(row: List<String>, rowNum: Int): Validated<CsvParseException, T?> {
        return if (column.index < 0 || column.index >= row.size ||
                   name.isEmpty() || row[column.index].isEmpty()
        ) Validated.Valid(null)
        else this.column.parseCell(row, rowNum)
    }

}
