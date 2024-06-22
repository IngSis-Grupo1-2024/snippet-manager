package manager.bucket.controller

import manager.bucket.service.ManagerServiceSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException

@RestController
class ManagerController @Autowired constructor(private val service: ManagerServiceSpec): ManagerControllerSpec{
    override fun createSnippet(snippetContent: String): ResponseEntity<String> {
        try{
            val responseBody = service.createSnippet(snippetContent)
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody)
        } catch(e: HttpClientErrorException){
            return ResponseEntity.status(e.statusCode).body(e.message)
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
            return ResponseEntity.ok("Snippet ${snippetId} deleted")
        } catch(e: HttpClientErrorException){
            return ResponseEntity.status(e.statusCode).body(e.message)
        }
    }


}