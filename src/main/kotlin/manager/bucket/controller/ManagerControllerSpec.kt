package manager.bucket.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/")
interface ManagerControllerSpec {

    @PostMapping
    fun createSnippet(@RequestBody snippetContent: String): ResponseEntity<String>

    @GetMapping("{snippetId}")
    fun getSnippet(@PathVariable snippetId: String) : ResponseEntity<String>

    @DeleteMapping("{snippetId}")
    fun deleteSnippet(@PathVariable snippetId: String) : ResponseEntity<String>
}