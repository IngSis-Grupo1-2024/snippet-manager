package manager.runner.controller

import manager.common.rest.BasicRest.Companion.getUserId
import manager.common.rest.dto.Output
import manager.runner.service.RunnerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.RestController
import snippet.SnippetFormatBody
import snippet.SnippetInfo

@RestController
class RunnerController
    @Autowired
    constructor(private val runnerService: RunnerService) : RunnerControllerSpec {
        override fun runSnippet(content: SnippetInfo): Output {
            return runnerService.runSnippet(content)
        }

        override fun formatSnippet(
            jwt: Jwt,
            snippetBody: SnippetFormatBody,
        ): Output {
            return runnerService.formatSnippet(snippetBody, getUserId(jwt.subject), jwt.tokenValue)
        }
    }
