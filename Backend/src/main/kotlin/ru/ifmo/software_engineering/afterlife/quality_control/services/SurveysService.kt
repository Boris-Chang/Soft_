package ru.ifmo.software_engineering.afterlife.quality_control.services

import arrow.core.Validated
import arrow.core.computations.nullable
import arrow.core.invalid
import arrow.core.valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.software_engineering.afterlife.core.exceptions.ApplicationException
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException
import ru.ifmo.software_engineering.afterlife.core.models.PageRequest
import ru.ifmo.software_engineering.afterlife.core.models.PagedResult
import ru.ifmo.software_engineering.afterlife.quality_control.domain.Survey
import ru.ifmo.software_engineering.afterlife.quality_control.repositories.SurveysRepository

interface SurveysService {
    suspend fun getSurveyById(id: Long): Survey?
    suspend fun getAllSurveys(pageRequest: PageRequest): PagedResult<Survey>
    suspend fun createSurvey(survey: Survey): Survey
    suspend fun updateSurvey(id: Long, survey: Survey): Validated<ApplicationException, Survey>
    suspend fun deleteSurvey(id: Long): Validated<ApplicationException, Unit>
}

@Service
class SurveysServiceImpl(
    private val surveysRepository: SurveysRepository
) : SurveysService {
    override suspend fun getSurveyById(id: Long): Survey? =
        this.surveysRepository.findById(id)

    override suspend fun getAllSurveys(pageRequest: PageRequest): PagedResult<Survey> =
        this.surveysRepository.findAll(pageRequest)

    override suspend fun createSurvey(survey: Survey): Survey =
        this.surveysRepository.save(survey)

    @Transactional
    override suspend fun updateSurvey(id: Long, survey: Survey): Validated<ApplicationException, Survey> =
        nullable {
            val currentSurvey = getSurveyById(id).bind()
            val surveyToUpdate = currentSurvey.copy(
                title = survey.title,
                url = survey.url,
                addressee = survey.addressee
            )
            surveysRepository.update(surveyToUpdate)
        }?.valid()
            ?: NotFoundException().invalid()

    override suspend fun deleteSurvey(id: Long): Validated<ApplicationException, Unit> =
        nullable {
            val currentSurvey = getSurveyById(id).bind()
            surveysRepository.delete(currentSurvey)
        }?.valid()
            ?: NotFoundException().invalid()
}
