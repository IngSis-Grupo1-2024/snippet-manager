package manager.manager.controller

import com.example.snippetmanager.snippet.CreateSnippet
import com.example.snippetmanager.snippet.UpdateSnippet
import manager.common.rest.dto.Output
import manager.common.rest.exception.ErrorOutput
import manager.common.rest.exception.NotFoundException
import manager.manager.model.dto.FileTypeDto
import manager.manager.model.dto.SnippetDto
import manager.manager.service.ManagerServiceSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException

@RestController
class ManagerController @Autowired constructor(private val service: ManagerServiceSpec): ManagerControllerSpec {
    override fun createSnippet(snippetContent: CreateSnippet): ResponseEntity<Output> {
        try{
            val responseBody = service.createSnippet(snippetContent)
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody)
        } catch(e: HttpClientErrorException){
            return ResponseEntity.status(e.statusCode).body(ErrorOutput(e.message!!))
        }
    }

    override fun getSnippet(snippetId: String): ResponseEntity<String> {
        try{
            val responseBody = service.getSnippet(snippetId)
            return ResponseEntity.ok(responseBody)
        } catch(e: HttpClientErrorException){
            return ResponseEntity.status(e.statusCode).body(e.message)
        }
    }

    override fun deleteSnippet(snippetId: String): ResponseEntity<String> {
        try{
            service.deleteSnippet(snippetId)
            return ResponseEntity.ok("Snippet $snippetId deleted")
        } catch(e: HttpClientErrorException){
            return ResponseEntity.status(e.statusCode).body(e.message)
        }
    }

    override fun updateSnippet(snippetId: String, snippetContent: UpdateSnippet): ResponseEntity<Output> {
        try{
            val responseBody: SnippetDto = service.updateSnippet(snippetId, snippetContent)
            return ResponseEntity.ok(responseBody)
        } catch(e: HttpClientErrorException){
            return ResponseEntity.status(e.statusCode).body(ErrorOutput(e.message!!))
        } catch(e: NotFoundException){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorOutput(e.message!!))
        }
    }

    override fun getFileTypes(): ResponseEntity<List<FileTypeDto>> {
        return ResponseEntity.status(HttpStatus.OK).body(this.service.getFileTypes())
    }


}