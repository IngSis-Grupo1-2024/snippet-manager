package bucket

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity

class BucketManager(private val urlForBucket: String, private val rest: RestTemplate) {

    fun createSnippet(snippetId: String, bodyContent: String): ResponseEntity<String> {
        val fullUrl = "$urlForBucket/$snippetId"
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val requestEntity = HttpEntity(bodyContent, headers)

        return rest.postForEntity(fullUrl, requestEntity, String::class.java)
    }

    fun getSnippet(snippetId: String) : ResponseEntity<String>{
        val url = "$urlForBucket/$snippetId"
        return rest.getForEntity(url, String::class.java)
    }

    fun deleteSnippet(snippetId: String) : ResponseEntity<String> {
        val url = "$urlForBucket/$snippetId"
        try {
            rest.delete(url)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }
        return ResponseEntity.ok().build()
    }
}
