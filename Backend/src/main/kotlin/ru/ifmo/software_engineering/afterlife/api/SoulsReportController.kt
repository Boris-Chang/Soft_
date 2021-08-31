package ru.ifmo.software_engineering.afterlife.api

import org.apache.commons.io.input.BOMInputStream
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.ifmo.software_engineering.afterlife.classificator.services.SoulReportsService
import ru.ifmo.software_engineering.afterlife.core.exceptions.BadRequestException
import ru.ifmo.software_engineering.afterlife.core.exceptions.NotFoundException

@RestController
@RequestMapping("api/souls/{soulId}/reports")
class SoulsReportController(
    private val soulReportsService: SoulReportsService,
) {
    @PutMapping("/sins", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun addOrUpdateSinsReport(
        @PathVariable("soulId")
        soulId: Long,
        @RequestPart(required = true, name = "file" )
        file: MultipartFile
    ) : ResponseEntity<Any> {
        return this.soulReportsService.saveOrUpdateSinsReportForSoulFromCsv(soulId, BOMInputStream(file.inputStream)).fold({
            when (it) {
                is BadRequestException -> ResponseEntity.badRequest().body(it.message)
                is NotFoundException -> ResponseEntity.notFound().build() }},
            { ResponseEntity.ok(it) }
        )
    }

    @PutMapping("/goodness", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun addOrUpdateGoodnessReport(
        @PathVariable("soulId")
        soulId: Long,
        @RequestPart(required = true, name = "file")
        file: MultipartFile
    ) : ResponseEntity<Any> {
        return this.soulReportsService.saveOrUpdateGoodnessReportForSoulFromCsv(soulId, BOMInputStream(file.inputStream)).fold({
            when (it) {
                is BadRequestException -> ResponseEntity.badRequest().body(it.message)
                is NotFoundException -> ResponseEntity.notFound().build() }},
            { ResponseEntity.ok(it) }
        )
    }
}