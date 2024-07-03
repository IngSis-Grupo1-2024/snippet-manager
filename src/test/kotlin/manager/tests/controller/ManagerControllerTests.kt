package manager.tests.controller

import manager.common.rest.exception.ErrorOutput
import manager.common.rest.exception.NotFoundException
import manager.manager.controller.ManagerController
import manager.manager.model.dto.FileTypeDto
import manager.manager.model.dto.SnippetDto
import manager.manager.model.dto.SnippetListDto
import manager.manager.model.dto.UsersDto
import manager.manager.model.enums.ComplianceSnippet
import manager.manager.model.enums.SnippetLanguage
import manager.manager.model.input.CreateSnippet
import manager.manager.model.input.ShareSnippetInput
import manager.manager.model.input.UpdateSnippet
import manager.manager.service.ManagerServiceSpec
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.server.ResponseStatusException

@ExtendWith(MockitoExtension::class)
class ManagerControllerTest {

    @Mock
    private lateinit var service: ManagerServiceSpec

    @InjectMocks
    private lateinit var controller: ManagerController

    @Test
    fun `saveName should return CREATED status with correct body`() {
        val jwtMock = mockJwt("userId")
        val name = "TestName"
        val expectedResponse = "ExpectedResponse"
        whenever(service.saveName(eq(name.dropLast(1)), eq("userId"))).thenReturn(expectedResponse)

        val response = controller.saveName(jwtMock, name)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(expectedResponse, response.body)
        verify(service).saveName(eq(name.dropLast(1)), eq("userId"))
    }

    @Test
    fun `createSnippet should return CREATED status with correct body`() {
        val jwtMock = mockJwt("userId")
        val snippetContent = CreateSnippet("name", "content", SnippetLanguage.PRINTSCRIPT, "ps")
        val expectedOutput =
            SnippetDto("name", "content", SnippetLanguage.PRINTSCRIPT, "ps", 1, ComplianceSnippet.PENDING, "1")
        whenever(service.createSnippet(eq(snippetContent), eq("userId"), any())).thenReturn(expectedOutput)

        val response = controller.createSnippet(jwtMock, snippetContent)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(expectedOutput, response.body)
        verify(service).createSnippet(eq(snippetContent), eq("userId"), any())
    }

    @Test
    fun `getSnippet should return OK status with correct body`() {
        val snippetId = "1"
        val expectedOutput = SnippetDto(
            "name",
            "content",
            SnippetLanguage.PRINTSCRIPT,
            "ps",
            1,
            manager.manager.model.enums.ComplianceSnippet.PENDING,
            snippetId
        )
        whenever(service.getSnippet(eq(snippetId))).thenReturn(expectedOutput)

        val response = controller.getSnippet(snippetId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedOutput, response.body)
        verify(service).getSnippet(eq(snippetId))
    }

    @Test
    fun `deleteSnippet should return OK status with correct message`() {
        val jwtMock = mockJwt("userId")
        val snippetId = "1"
        val expectedMessage = "Snippet $snippetId deleted"

        val response = controller.deleteSnippet(jwtMock, snippetId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedMessage, response.body)
        verify(service).deleteSnippet(eq("userId"), any(), eq(snippetId))
    }

    @Test
    fun `updateSnippet should return OK status with correct body`() {
        val jwtMock = mockJwt("userId")
        val snippetId = "1"
        val updateContent = UpdateSnippet("Updated content")
        val expectedDto = SnippetDto(
            "name",
            "Updated content",
            SnippetLanguage.PRINTSCRIPT,
            "ps",
            1,
            ComplianceSnippet.PENDING,
            snippetId
        )
        whenever(service.updateSnippet(eq(snippetId), eq(updateContent.content), eq("userId"), any())).thenReturn(
            expectedDto
        )

        val response = controller.updateSnippet(snippetId, updateContent, jwtMock)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedDto, response.body)
        verify(service).updateSnippet(eq(snippetId), eq(updateContent.content), eq("userId"), any())
    }

    @Test
    fun `getFileTypes should return OK status with correct body`() {
        val expectedFileTypes = listOf(
            FileTypeDto("Java", "java"),
            FileTypeDto("PrintScript", "ps")
        )
        whenever(service.getFileTypes()).thenReturn(expectedFileTypes)

        val response = controller.getFileTypes()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedFileTypes, response.body)
        verify(service).getFileTypes()
    }

    @Test
    fun `getSnippetDescriptors should return OK status with correct body`() {
        val jwtMock = mockJwt("userId")
        val expectedListDto = SnippetListDto(listOf())
        whenever(service.getSnippetDescriptors(eq("userId"), any())).thenReturn(expectedListDto)

        val response = controller.getSnippetDescriptors(jwtMock)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedListDto, response.body)
        verify(service).getSnippetDescriptors(eq("userId"), any())
    }

    @Test
    fun `getUserFriends should return OK status with correct body`() {
        val jwtMock = mockJwt("userId")
        val expectedUsersDto = UsersDto(listOf())
        whenever(service.getUserFriends(eq("userId"))).thenReturn(expectedUsersDto)

        val response = controller.getUserFriends(jwtMock)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedUsersDto, response.body)
        verify(service).getUserFriends(eq("userId"))
    }

    @Test
    fun `shareSnippet should return OK status with correct body`() {
        val jwtMock = mockJwt("userId")
        val shareSnippetInput = ShareSnippetInput("recipientUserId", "snippetId")
        val expectedOutput =
            SnippetDto("name", "content", SnippetLanguage.PRINTSCRIPT, "ps", 1, ComplianceSnippet.PENDING, "1")
        whenever(service.shareSnippet(eq("userId"), eq(shareSnippetInput), any())).thenReturn(expectedOutput)

        val response = controller.shareSnippet(jwtMock, shareSnippetInput)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedOutput, response.body)
        verify(service).shareSnippet(eq("userId"), eq(shareSnippetInput), any())
    }

    @Test
    fun `createSnippet should handle HttpClientErrorException correctly`() {
        val jwtMock = mockJwt("userId")
        val snippetContent = CreateSnippet("name", "content", SnippetLanguage.PRINTSCRIPT, "ps")
        val errorMessage = "Error from client"
        whenever(service.createSnippet(eq(snippetContent), eq("userId"), any()))
            .thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST, errorMessage))

        val response = controller.createSnippet(jwtMock, snippetContent)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `createSnippet should handle NotFoundException correctly`() {
        val jwtMock = mockJwt("userId")
        val snippetContent = CreateSnippet("name", "content", SnippetLanguage.PRINTSCRIPT, "ps")
        val errorMessage = "Snippet not found"
        whenever(service.createSnippet(eq(snippetContent), eq("userId"), any()))
            .thenThrow(NotFoundException(errorMessage))

        val response = controller.createSnippet(jwtMock, snippetContent)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `getSnippet should handle HttpClientErrorException correctly`() {
        val snippetId = "testSnippetId"
        val errorMessage = "Client error"
        whenever(service.getSnippet(eq(snippetId)))
            .thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST, errorMessage))

        val response = controller.getSnippet(snippetId)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `deleteSnippet should handle HttpClientErrorException correctly`() {
        val jwtMock = mockJwt("userId")
        val snippetId = "testSnippetId"
        val errorMessage = "Client error"
        whenever(service.deleteSnippet(eq("userId"), any(), eq(snippetId)))
            .thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST, errorMessage))

        val response = controller.deleteSnippet(jwtMock, snippetId)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `updateSnippet should handle HttpClientErrorException correctly`() {
        val jwtMock = mockJwt("userId")
        val snippetId = "testSnippetId"
        val snippetContent = UpdateSnippet("Updated content")
        val errorMessage = "Client error"
        whenever(service.updateSnippet(eq(snippetId), eq(snippetContent.content), eq("userId"), any()))
            .thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST, errorMessage))

        val response = controller.updateSnippet(snippetId, snippetContent, jwtMock)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `updateSnippet should handle NotFoundException correctly`() {
        val jwtMock = mockJwt("userId")
        val snippetId = "testSnippetId"
        val snippetContent = UpdateSnippet("Updated content")
        val errorMessage = "Snippet not found"
        whenever(service.updateSnippet(eq(snippetId), eq(snippetContent.content), eq("userId"), any()))
            .thenThrow(NotFoundException(errorMessage))

        val response = controller.updateSnippet(snippetId, snippetContent, jwtMock)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `getSnippetDescriptors should handle HttpClientErrorException correctly`() {
        val jwtMock = mockJwt("userId")
        val errorMessage = "Client error"
        whenever(service.getSnippetDescriptors(eq("userId"), any()))
            .thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST, errorMessage))

        val response = controller.getSnippetDescriptors(jwtMock)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `getSnippetDescriptors should handle NotFoundException correctly`() {
        val jwtMock = mockJwt("userId")
        val errorMessage = "Snippet not found"
        whenever(service.getSnippetDescriptors(eq("userId"), any()))
            .thenThrow(NotFoundException(errorMessage))

        val response = controller.getSnippetDescriptors(jwtMock)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    private fun mockJwt(subject: String): Jwt {
        return Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", subject)
            .build()
    }
}
