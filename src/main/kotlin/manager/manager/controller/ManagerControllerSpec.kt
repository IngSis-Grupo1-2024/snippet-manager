package manager.manager.controller

import manager.manager.model.input.CreateSnippet
import com.example.snippetmanager.snippet.UpdateSnippet
import manager.common.rest.dto.Output
import manager.manager.model.dto.FileTypeDto
import manager.manager.model.input.ShareSnippetInput
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RequestMapping("/")
interface ManagerControllerSpec {
    @PostMapping("/userName")
    fun saveName(@AuthenticationPrincipal jwt: Jwt, @RequestBody name: String): ResponseEntity<String>

    @PostMapping
    fun createSnippet(@AuthenticationPrincipal jwt: Jwt, @RequestBody snippetContent: CreateSnippet): ResponseEntity<Output>

    @GetMapping("{snippetId}")
    fun getSnippet(@PathVariable snippetId: String) : ResponseEntity<Output>

    @DeleteMapping("{snippetId}")
    fun deleteSnippet(@AuthenticationPrincipal jwt: Jwt, @PathVariable snippetId: String) : ResponseEntity<String>

    @PutMapping("{snippetId}")
    fun updateSnippet(@PathVariable snippetId: String, @RequestBody snippetContent: UpdateSnippet): ResponseEntity<Output>

    @GetMapping("fileType")
    fun getFileTypes(): ResponseEntity<List<FileTypeDto>>

    @GetMapping("snippetDescriptors")
    fun getSnippetDescriptors(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<Output>

    @GetMapping("users")
    fun getUserFriends(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<Output>

    @PostMapping("share")
    fun shareSnippet(@AuthenticationPrincipal jwt: Jwt, @RequestBody shareSnippet: ShareSnippetInput): ResponseEntity<Output>
}