package ru.ifmo.software_engineering.afterlife.classificator.services

import org.springframework.stereotype.Service
import ru.ifmo.software_engineering.afterlife.classificator.domain.AfterworldSectionClassifier
import ru.ifmo.software_engineering.afterlife.classificator.domain.ReportedSoul
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.classificator.repositories.AfterworldSectionRepository
import ru.ifmo.software_engineering.afterlife.classificator.repositories.GoodnessReportRepository
import ru.ifmo.software_engineering.afterlife.classificator.repositories.SinsReportRepository

interface SoulClassifierService {
    suspend fun classifySoulIfRequired(soul: Soul): ReportedSoul
}

@Service
class SoulClassifierServiceImpl(
        private val goodnessReportRepository: GoodnessReportRepository,
        private val sinsReportRepository: SinsReportRepository,
        private val afterworldSectionClassifier: AfterworldSectionClassifier,
        private val afterworldSectionRepository: AfterworldSectionRepository
) : SoulClassifierService {
    override suspend fun classifySoulIfRequired(soul: Soul): ReportedSoul {
        val goodnessReport = goodnessReportRepository.findBySoul(soul)
        val sinsReport = sinsReportRepository.findBySoul(soul)

        if (goodnessReport == null || sinsReport == null) {
            return ReportedSoul(soul, sinsReport, goodnessReport)
        }

        val classifiedSection = afterworldSectionClassifier.classifyAfterworldSection(sinsReport.sins, goodnessReport.goodnessEvidences)

        afterworldSectionRepository.saveOrUpdateAfterworldSectionForSoul(soul, classifiedSection)
        //TODO: update soul section
        return ReportedSoul(soul, sinsReport, goodnessReport)
    }

}