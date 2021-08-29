package ru.ifmo.software_engineering.afterlife.classificator.report_readers.csv

import arrow.core.Validated
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.boot.test.context.SpringBootTest
import ru.ifmo.software_engineering.afterlife.utils.UtcZone
import ru.ifmo.software_engineering.afterlife.utils.toUtc
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.stream.Stream
import kotlin.test.assertEquals

@SpringBootTest
class CsvColumnTest {
    @ParameterizedTest
    @MethodSource("dateColumnTestData")
    fun `when Column of Date type provided column Should be parsed as Date at provided index`(
        expectedDate: ZonedDateTime?, col: CsvColumn<ZonedDateTime>, row: List<String>
    ) {
        val rowNum = 1
        val actualDateUtc = col.parseCell(row, rowNum)
            .fold({null}, {it.toUtc()})

        assertEquals(expectedDate?.toUtc(), actualDateUtc)
    }

    enum class TestEnum {
        A1, A2, A3
    }

    @ParameterizedTest
    @MethodSource("enumColumnTestData")
    fun `when Column of Enum type provided column Should be parsed as Enum at provided index`(
        expectedEnum: TestEnum?, col: CsvColumn<TestEnum>, row: List<String>
    ) {
        val rowNum = 1
        val actualEnum = col.parseCell(row, rowNum).fold({null}, {it})

        assertEquals(expectedEnum, actualEnum)
    }

    @ParameterizedTest
    @MethodSource("optionalEnumColumnTestData")
    fun `when Column of Optional Enum type provided column Should be parsed as Enum at provided index`(
        expectedResult: Validated<CsvParseException, TestEnum?>, col: CsvColumn<TestEnum?>, row: List<String>
    ) {
        val rowNum = 1
        val actualResult = col.parseCell(row, rowNum)

        assertEquals(expectedResult, actualResult)
    }

    companion object {
        @JvmStatic
        fun dateColumnTestData(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    ZonedDateTime.of(
                        LocalDateTime.of(2021, 1, 3, 12, 45, 12),
                        UtcZone),
                    DateCsvColumn(0, "Date"),
                    listOf("2021-01-03T12:45:12Z", "", "")),
                Arguments.of(
                    ZonedDateTime.of(
                        LocalDateTime.of(2021, 1, 3, 9, 45, 12),
                        UtcZone),
                    DateCsvColumn(2, "Date"),
                    listOf("", "", "2021-01-03T12:45:12+03:00")),
                Arguments.of(
                    ZonedDateTime.of(
                        LocalDateTime.of(2021, 1, 3, 15, 45, 12),
                        UtcZone),
                    DateCsvColumn(2, "Date"),
                    listOf("", "", "2021-01-03T12:45:12-03:00")),
                Arguments.of(
                    null,
                    DateCsvColumn(3, "Date"),
                    listOf("", "", "2021-01-03T12:45:12Z-03:00")),
                Arguments.of(
                    null,
                    DateCsvColumn(-1, "Date"),
                    listOf("", "", "2021-01-03T12:45:12Z-03:00")),
            )

        @JvmStatic
        fun enumColumnTestData(): Stream<Arguments> = Stream.of(
            Arguments.of(
                TestEnum.A1,
                EnumCsvColumn(0, "Enum", TestEnum::class),
                listOf("A1", "", "")),
            Arguments.of(
                TestEnum.A3,
                EnumCsvColumn(0, "Enum", TestEnum::class),
                listOf("A3", "", "")),
            Arguments.of(
                TestEnum.A2,
                EnumCsvColumn(2, "Enum", TestEnum::class),
                listOf("", "", "A2")),
            Arguments.of(
                null,
                EnumCsvColumn(0, "Enum", TestEnum::class),
                listOf("Ae", "", "")),
            Arguments.of(
                null,
                EnumCsvColumn(-1, "Enum", TestEnum::class),
                listOf("A3", "", "")),
            Arguments.of(
                null,
                EnumCsvColumn(3, "Enum", TestEnum::class),
                listOf("A3", "", "A3")),
        )

        @JvmStatic
        fun optionalEnumColumnTestData(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Validated.Valid(null),
                OptionalColumn(
                    EnumCsvColumn(-1, "Enum", TestEnum::class)),
                listOf("A3", "", "")),
            Arguments.of(
                Validated.Valid(null),
                OptionalColumn(
                    EnumCsvColumn(3, "Enum", TestEnum::class)),
                listOf("A3", "", "A3")),
            Arguments.of(
                Validated.Valid(null),
                OptionalColumn(
                    EnumCsvColumn(2, "Enum", TestEnum::class)),
                listOf("A3", "", "")),
            Arguments.of(
                Validated.Valid(TestEnum.A3),
                OptionalColumn(
                    EnumCsvColumn(0, "Enum", TestEnum::class)),
                listOf("A3", "", "")),
        )
    }
}