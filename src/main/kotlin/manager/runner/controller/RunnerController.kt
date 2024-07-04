package manager.runner.controller

import manager.rules.controller.RulesController
import manager.runner.service.RunnerService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.RestController

@RestController
class RunnerController
    @Autowired
    constructor(private val runnerService: RunnerService) : RunnerControllerSpec {
    private val logger = LoggerFactory.getLogger(RunnerController::class.java)
        override fun runSnippet(
            jwt: Jwt,
            snippetId: String,
        ): ResponseEntity<String> {
            try {
                logger.info("Sending to execute snippet to the snippet runner")
                val output = runnerService.runSnippet(jwt.tokenValue, snippetId)
                return ResponseEntity(output, HttpStatus.OK)
            } catch (e: Exception) {
                logger.warn(e.message)
                return ResponseEntity(e.message!!, HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }

        override fun formatSnippet(
            jwt: Jwt,
            snippetId: String,
        ): ResponseEntity<String> {
            try {
                logger.info("Sending to format snippet to the snippet runner")
                val output = runnerService.formatSnippet(snippetId, jwt.subject, jwt.tokenValue)
                return ResponseEntity(output, HttpStatus.OK)
            } catch (e: Exception) {
                logger.warn(e.message)
                return ResponseEntity(e.message!!, HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }
