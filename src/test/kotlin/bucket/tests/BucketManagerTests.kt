package bucket.tests

import manager.common.bucket.BucketConfig
import manager.common.bucket.BucketManager
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

class BucketManagerTest {
    private lateinit var bucketManager: BucketManager
    private lateinit var bucketConfig: BucketConfig
    private val restTemplate = Mockito.mock(RestTemplate::class.java)
    private val urlForBucket = "http://localhost:8080/bucket"

    @BeforeEach
    fun setup() {
        bucketConfig = BucketConfig(restTemplate, urlForBucket)
        bucketManager = bucketConfig.createRemoteBucketApi()
    }

    @Test
    fun `test createSnippet`() {
        val snippetId = "snippet1"
        val bodyContent = "This is a test snippet"
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val requestEntity = HttpEntity(bodyContent, headers)
        val responseEntity = ResponseEntity(bodyContent, HttpStatus.CREATED)

        Mockito.`when`(restTemplate.postForEntity("$urlForBucket/$snippetId", requestEntity, String::class.java)).thenReturn(responseEntity)

        val result = bucketManager.createSnippet(snippetId, bodyContent)

        assertEquals(bodyContent, result)
    }

    @Test
    fun `test createSnippet with error`() {
        val snippetId = "snippet1"
        val bodyContent = "This is a test snippet"
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val requestEntity = HttpEntity(bodyContent, headers)
        val responseEntity = ResponseEntity(bodyContent, HttpStatus.BAD_REQUEST)

        Mockito.`when`(restTemplate.postForEntity("$urlForBucket/$snippetId", requestEntity, String::class.java)).thenReturn(responseEntity)

        assertThrows(HttpClientErrorException::class.java) {
            bucketManager.createSnippet(snippetId, bodyContent)
        }
    }

    @Test
    fun `test getSnippet`() {
        val snippetId = "snippet1"
        val bodyContent = "This is a test snippet"
        val responseEntity = ResponseEntity(bodyContent, HttpStatus.OK)

        Mockito.`when`(restTemplate.getForEntity("$urlForBucket/$snippetId", String::class.java)).thenReturn(responseEntity)

        val result = bucketManager.getSnippet(snippetId)

        assertEquals(bodyContent, result)
    }

    @Test
    fun `test getSnippet with error`() {
        val snippetId = "snippet1"
        val responseEntity = ResponseEntity("", HttpStatus.NOT_FOUND)

        Mockito.`when`(restTemplate.getForEntity("$urlForBucket/$snippetId", String::class.java)).thenReturn(responseEntity)

        assertThrows(HttpClientErrorException::class.java) {
            bucketManager.getSnippet(snippetId)
        }
    }

    @Test
    fun `test deleteSnippet`() {
        val snippetId = "snippet1"

        Mockito.doNothing().`when`(restTemplate).delete("$urlForBucket/$snippetId")

        bucketManager.deleteSnippet(snippetId)

        Mockito.verify(restTemplate, Mockito.times(1)).delete("$urlForBucket/$snippetId")
    }

    @Test
    fun `test deleteSnippet with error`() {
        val snippetId = "snippet1"

        Mockito.doThrow(HttpClientErrorException(HttpStatus.NOT_FOUND)).`when`(restTemplate).delete("$urlForBucket/$snippetId")

        assertThrows(HttpClientErrorException::class.java) {
            bucketManager.deleteSnippet(snippetId)
        }
    }
}
