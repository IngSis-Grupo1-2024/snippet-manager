package manager.bucket.service

import org.springframework.http.HttpStatus

interface ManagerServiceSpec {
    fun createSnippet(snippetContent: String): String?

    fun getSnippet(snippetId: String) : String

    fun deleteSnippet(snippetId: String)
}