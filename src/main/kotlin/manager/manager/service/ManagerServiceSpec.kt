package manager.manager.service

import com.example.snippetmanager.snippet.CreateSnippet
import com.example.snippetmanager.snippet.UpdateSnippet
import manager.manager.model.dto.FileTypeDto
import manager.manager.model.dto.SnippetDto

interface ManagerServiceSpec {
    fun createSnippet(input: CreateSnippet): SnippetDto

    fun getSnippet(snippetId: String) : String

    fun deleteSnippet(snippetId: String)

    fun updateSnippet(snippetId: String, input: UpdateSnippet): SnippetDto
    fun getFileTypes(): List<FileTypeDto>
}