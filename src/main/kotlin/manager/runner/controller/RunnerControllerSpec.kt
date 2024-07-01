package manager.runner.controller

import manager.common.rest.dto.Output
import manager.snippet.SnippetInfo
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RequestMapping("/run")
interface RunnerControllerSpec {
    @PostMapping("/execute/{snippetId}")
    fun runSnippet(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable snippetId: String,
    ): ResponseEntity<String>

    @PostMapping("/format/{snippetId}")
    fun formatSnippet(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable snippetId: String,
    ): ResponseEntity<String>
}
