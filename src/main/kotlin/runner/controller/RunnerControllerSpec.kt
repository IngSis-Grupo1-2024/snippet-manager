package runner.controller

import com.example.snippetmanager.snippet.CreateSnippet
import com.example.snippetmanager.snippet.SnippetInfo
import com.example.snippetmanager.snippet.UpdateSnippet
import manager.common.rest.dto.Output
import manager.manager.model.dto.FileTypeDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import runner.RunningOutput

@RequestMapping("/run")
interface RunnerControllerSpec {
    @PostMapping
    fun runSnippet(@RequestBody content: SnippetInfo) : RunningOutput

}