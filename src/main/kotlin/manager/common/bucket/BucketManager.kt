package manager.common.bucket

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

class BucketManager(private val urlForBucket: String, private val rest: RestTemplate) : BucketAPI {
    override fun createSnippet(
        snippetId: String,
        bodyContent: String,
    ): String? {
        println(urlForBucket)
        val fullUrl = "$urlForBucket/$snippetId"
        val headers =
            HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }
        val requestEntity = HttpEntity(bodyContent, headers)

        val response = rest.postForEntity(fullUrl, requestEntity, String::class.java)
        if (response.statusCode != HttpStatus.CREATED) {
            throw HttpClientErrorException(response.statusCode, response.body!!)
        }
        return response.body
    }

    override fun getSnippet(snippetId: String): String {
        val url = "$urlForBucket/$snippetId"
        val response = rest.getForEntity(url, String::class.java)
        if (response.statusCode != HttpStatus.OK) {
            throw HttpClientErrorException(response.statusCode, response.body!!)
        }
        return response.body!!
    }

    override fun deleteSnippet(snippetId: String) {
        val url = "$urlForBucket/$snippetId"
        try {
            rest.delete(url)
        } catch (e: RestClientException) {
            throw HttpClientErrorException(HttpStatus.NOT_FOUND, "Asset was not found")
        }
    }
}
