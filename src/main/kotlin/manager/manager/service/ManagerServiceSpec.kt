package manager.manager.service

import com.example.snippetmanager.snippet.CreateSnippet
import com.example.snippetmanager.snippet.UpdateSnippet
import manager.manager.model.dto.SnippetDto
import org.springframework.http.HttpStatus

interface ManagerServiceSpec {
    fun createSnippet(snippetContent: CreateSnippet): SnippetDto

    fun getSnippet(snippetId: String) : String

    fun deleteSnippet(snippetId: String)

    fun updateSnippet(snippetId: String, snippetContent: UpdateSnippet): SnippetDto
}