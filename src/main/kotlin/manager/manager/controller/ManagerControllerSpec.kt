package manager.manager.controller

import com.example.snippetmanager.snippet.CreateSnippet
import com.example.snippetmanager.snippet.UpdateSnippet
import manager.common.rest.dto.Output
import manager.manager.model.dto.SnippetDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/")
interface ManagerControllerSpec {

    @PostMapping
    fun createSnippet(@RequestBody snippetContent: CreateSnippet): ResponseEntity<Output>

    @GetMapping("{snippetId}")
    fun getSnippet(@PathVariable snippetId: String) : ResponseEntity<String>

    @DeleteMapping("{snippetId}")
    fun deleteSnippet(@PathVariable snippetId: String) : ResponseEntity<String>

    @PutMapping("{snippetId}")
    fun updateSnippet(@PathVariable snippetId: String, @RequestBody snippetContent: UpdateSnippet): ResponseEntity<Output>
}