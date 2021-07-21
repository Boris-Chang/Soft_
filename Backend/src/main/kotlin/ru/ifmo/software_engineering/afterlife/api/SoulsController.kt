package ru.ifmo.software_engineering.afterlife.api

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.software_engineering.afterlife.classificator.domain.Soul
import ru.ifmo.software_engineering.afterlife.classificator.services.SoulRegistrar

@RestController
@RequestMapping("api/souls")
class SoulsController(
    private val soulsRegistrar: SoulRegistrar
) {
    @PostMapping
    suspend fun registerNewSoul(@RequestBody soul: Soul): Soul {
        return this.soulsRegistrar.registerNewSoul(soul)
    }
}
