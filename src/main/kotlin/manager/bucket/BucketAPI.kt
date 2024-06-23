package manager.bucket

import org.springframework.http.HttpStatus

interface BucketAPI {
    fun createSnippet(snippetId: String, bodyContent: String): String?

    fun getSnippet(snippetId: String) : String

    fun deleteSnippet(snippetId: String)
}