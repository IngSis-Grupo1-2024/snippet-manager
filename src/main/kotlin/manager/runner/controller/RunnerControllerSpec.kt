package manager.runner.controller

import manager.common.rest.dto.Output
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import snippet.SnippetFormatBody
import snippet.SnippetInfo

@RequestMapping("/run")
interface RunnerControllerSpec {
    @PostMapping("/execute")
    fun runSnippet(
        @RequestBody content: SnippetInfo,
    ): Output

    @PostMapping("/format")
    fun formatSnippet(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody snippetBody: SnippetFormatBody,
    ): Output
}
