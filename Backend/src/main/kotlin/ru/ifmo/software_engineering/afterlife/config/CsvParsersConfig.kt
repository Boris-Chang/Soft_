package ru.ifmo.software_engineering.afterlife.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.ifmo.software_engineering.afterlife.classificator.domain.GoodnessEvidence
import ru.ifmo.software_engineering.afterlife.classificator.domain.SinEvidence
import ru.ifmo.software_engineering.afterlife.classificator.report_readers.csv.*

@Configuration
class CsvParsersConfig {
    @Bean
    fun goodnessEvidencesParser(): CsvParser<GoodnessEvidence> =
        GoodnessEvidencesCsvParser(
            GoodnessCsvRowParserProvider(
                GoodnessCsvRowParserProvider.GoodnessCsvHeaderNames(
                    "Goodness Kind",
                    "Date",
                )))

    @Bean
    fun sinEvidencesParser(): CsvParser<SinEvidence> =
        SinEvidencesCsvParser(
            SinCsvRowParserProvider(
                SinCsvRowParserProvider.SinCsvHeaderNames(
                    "Date",
                    "Sin Kind",
                    "Atoned At"
                )
            )
        )
}