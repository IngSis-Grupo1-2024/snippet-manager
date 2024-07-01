package manager.runner.controller

import manager.common.rest.dto.Output
import manager.runner.service.RunnerService
import manager.snippet.SnippetInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.RestController

@RestController
class RunnerController
    @Autowired
    constructor(private val runnerService: RunnerService) : RunnerControllerSpec {
        override fun runSnippet(content: SnippetInfo): Output {
            return runnerService.runSnippet(content)
        }

        override fun formatSnippet(
            jwt: Jwt,
            snippetId: String,
        ): ResponseEntity<String> {
            try {
                val output = runnerService.formatSnippet(snippetId, jwt.subject, jwt.tokenValue)
                return ResponseEntity(output, HttpStatus.OK)
            } catch (e: Exception) {
                return ResponseEntity(e.message!!, HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }
