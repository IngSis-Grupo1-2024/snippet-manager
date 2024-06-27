package manager.manager.service

import manager.manager.model.input.CreateSnippet
import com.example.snippetmanager.snippet.UpdateSnippet
import manager.manager.model.dto.FileTypeDto
import manager.manager.model.dto.SnippetDto
import manager.manager.model.dto.SnippetListDto
import manager.manager.model.dto.UsersDto
import manager.manager.model.input.ShareSnippetInput

interface ManagerServiceSpec {
    fun createSnippet(input: CreateSnippet, userId: String, token: String): SnippetDto

    fun getSnippet(snippetId: String) : SnippetDto

    fun deleteSnippet(snippetId: String)

    fun updateSnippet(snippetId: String, input: UpdateSnippet): SnippetDto
    fun getFileTypes(): List<FileTypeDto>
    fun saveName(name: String, userId: String): String
    fun getSnippetDescriptors(userId: String, token: String): SnippetListDto
    fun getUserFriends(userId: String): UsersDto
    fun shareSnippet(userId: String, shareSnippet: ShareSnippetInput, token: String): SnippetDto
}