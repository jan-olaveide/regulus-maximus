package no.nav.tsm.mottak.sykmelding.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.tsm.mottak.service.SykmeldingService
import no.nav.tsm.mottak.sykmelding.kafka.model.SykmeldingMedBehandlingsutfall
import no.nav.tsm.mottak.sykmelding.kafka.util.SykmeldingModule
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class SykmeldingConsumer(
    // private val kafkaTemplate: KafkaTemplate<String, SykmeldingMedUtfall>,
    private val sykmeldingService: SykmeldingService) {
    private val log = LoggerFactory.getLogger(SykmeldingConsumer::class.java)

    @KafkaListener(topics = ["\${topics.mottatt-sykmelding}"],
        groupId = "\${spring.kafka.group-id}",
        containerFactory = "containerFactory")
    suspend fun consume(sykmelding: SykmeldingMedBehandlingsutfall) {
        log.info("Mottok sykmelding $sykmelding")
        sykmeldingService.saveSykmelding(sykmelding)

        // her skal videre funksjonalitet ligge
        /* try {
             kafkaTemplate.send("tsm.sykmelding", SykmeldingMedUtfall(sykmeldingInput = sykmelding.sykmeldingInput, utfall = sykmelding.utfall))
         } catch (ex: Exception) {
             logger.error("Failed to publish sykmelding to tsm.sykmelding", ex)
         }*/

    }
}

val objectMapper: ObjectMapper =
    ObjectMapper().apply {
        registerKotlinModule()
        registerModule(SykmeldingModule())
        registerModule(JavaTimeModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }
