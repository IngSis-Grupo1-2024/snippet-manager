package manager.tests.service

import kotlinx.coroutines.ExperimentalCoroutinesApi
import manager.common.bucket.BucketAPI
import manager.common.rest.exception.BadReqException
import manager.manager.integration.permission.SnippetPerm
import manager.manager.model.dto.AddPermDto
import manager.manager.model.dto.SnippetDto
import manager.manager.model.entity.Snippet
import manager.manager.model.entity.SnippetStatus
import manager.manager.model.entity.UserSnippet
import manager.manager.model.enums.ComplianceSnippet
import manager.manager.model.enums.FileType
import manager.manager.model.enums.PermissionType
import manager.manager.model.enums.SnippetLanguage
import manager.manager.model.input.CreateSnippet
import manager.manager.model.input.ShareSnippetInput
import manager.manager.repository.SnippetRepository
import manager.manager.repository.SnippetStatusRepository
import manager.manager.repository.UserRepository
import manager.manager.service.ManagerService
import manager.redis.producer.LintProducer
import manager.rules.integration.configuration.SnippetConf
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.check
import org.mockito.kotlin.whenever
import java.util.*

@ExperimentalCoroutinesApi
class ManagerServiceTest {

    @Mock
    private lateinit var bucketAPI: BucketAPI

    @Mock
    private lateinit var snippetRepository: SnippetRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var snippetPerm: SnippetPerm

    @Mock
    private lateinit var snippetStatusRepository: SnippetStatusRepository

    @Mock
    private lateinit var lintProducer: LintProducer

    @Mock
    private lateinit var snippetConf: SnippetConf

    @InjectMocks
    private lateinit var managerService: ManagerService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `getSnippet should return the snippet`() {
        val snippetId = "1"
        val snippet = Optional.of(
            Snippet(
                "Test Snippet",
                SnippetLanguage.PRINTSCRIPT,
                "ps",
                UserSnippet("user1", "User Name"),
                SnippetStatus(ComplianceSnippet.PENDING)
            ).apply { id = 1L })
        val content = "Snippet Content"

        `when`(snippetRepository.findById(snippetId.toLong())).thenReturn(snippet)
        `when`(bucketAPI.getSnippet(snippetId)).thenReturn(content)

        val result = managerService.getSnippet(snippetId)

        assertNotNull(result)
        assertEquals("Test Snippet", result.name)
        assertEquals("Snippet Content", result.content)
    }

    @Test
    fun `deleteSnippet should delete the snippet`() {
        val snippetId = "1"
        val userId = "user1"
        val token = "token123"

        `when`(snippetPerm.getPermissionType(snippetId, userId, token)).thenReturn(PermissionType.OWNER)

        managerService.deleteSnippet(userId, token, snippetId)

        verify(snippetRepository, times(1)).deleteById(snippetId.toLong())
        verify(bucketAPI, times(1)).deleteSnippet(snippetId)
    }

    @Test
    fun `updateSnippet should update the snippet content`() {
        val snippetId = "1"
        val newContent = "New Content"
        val userId = "user1"
        val token = "token123"
        val snippet = Optional.of(
            Snippet(
                "Test Snippet",
                SnippetLanguage.PRINTSCRIPT,
                "ps",
                UserSnippet("user1", "User Name"),
                SnippetStatus(ComplianceSnippet.PENDING)
            ).apply { id = 1L })

        `when`(snippetPerm.getPermissionType(snippetId, userId, token)).thenReturn(PermissionType.OWNER)
        `when`(snippetRepository.findById(snippetId.toLong())).thenReturn(snippet)

        val result = managerService.updateSnippet(snippetId, newContent, userId, token)

        assertNotNull(result)
        assertEquals("New Content", result.content)
        verify(bucketAPI, times(1)).deleteSnippet(snippetId)
        verify(bucketAPI, times(1)).createSnippet(snippetId, newContent)
    }

    @Test
    fun `saveName should save the user name`() {
        val userId = "user1"
        val name = "New Name"
        val user = UserSnippet(userId, "Old Name")

        `when`(userRepository.findByUserId(userId)).thenReturn(user)
        `when`(userRepository.save(any(UserSnippet::class.java))).thenReturn(user)

        val result = managerService.saveName(name, userId)

        assertNotNull(result)
        assertEquals("User saved successfully", result)
        assertEquals(name, user.name)
    }

    @Test
    fun `getSnippetDescriptors should return a list of snippets`() {
        val userId = "user1"
        val token = "token123"
        val user = UserSnippet(userId, "User Name")
        val snippet = Snippet(
            "Test Snippet",
            SnippetLanguage.PRINTSCRIPT,
            "ps",
            user,
            SnippetStatus(ComplianceSnippet.PENDING)
        ).apply { id = 1L }
        user.snippet = listOf(snippet)

        `when`(userRepository.findByUserId(userId)).thenReturn(user)
        `when`(bucketAPI.getSnippet(snippet.id.toString())).thenReturn("Snippet Content")
        `when`(snippetPerm.getSharedSnippets(userId, token)).thenReturn(listOf())

        val result = managerService.getSnippetDescriptors(userId, token)

        assertNotNull(result)
        assertTrue(result.snippets.size == 1)
        assertEquals("Test Snippet", result.snippets[0].name)
    }

    @Test
    fun `getUserFriends should return a list of user friends`() {
        val userId = "user1"
        val users = listOf(
            UserSnippet("user2", "User 2"),
            UserSnippet("user3", "User 3"),
        )

        `when`(userRepository.findAll()).thenReturn(users)

        val result = managerService.getUserFriends(userId)

        assertNotNull(result)
        assertTrue(result.users.size == 2)
        assertEquals("User 2", result.users[0].name)
    }


    @Test
    fun `updateSnippetStatus should update the snippet status`() {
        val userId = "user1"
        val snippetId = "1"
        val complianceSnippet = ComplianceSnippet.COMPLIANT
        val snippet = Optional.of(
            Snippet(
                "Test Snippet",
                SnippetLanguage.PRINTSCRIPT,
                "ps",
                UserSnippet("user1", "User Name"),
                SnippetStatus(ComplianceSnippet.PENDING)
            ).apply { id = 1L })
        val status = SnippetStatus(complianceSnippet)

        `when`(snippetRepository.findById(snippetId.toLong())).thenReturn(snippet)
        `when`(snippetStatusRepository.findByStatus(complianceSnippet)).thenReturn(status)
        `when`(snippetRepository.save(any(Snippet::class.java))).thenReturn(snippet.get())

        val result = managerService.updateSnippetStatus(userId, snippetId, complianceSnippet)

        assertNotNull(result)
        assertEquals(complianceSnippet, result)
        assertEquals(complianceSnippet, snippet.get().status.status)
    }

    @Test
    fun `deleteSnippet should throw BadReqException when user is not the owner`() {
        val snippetId = "1"
        val userId = "user1"
        val token = "token123"

        `when`(snippetPerm.getPermissionType(snippetId, userId, token)).thenReturn(PermissionType.R)

        val exception = assertThrows<BadReqException> {
            managerService.deleteSnippet(userId, token, snippetId)
        }

        assertEquals("The user has no permissions for updating the snippet", exception.message)
    }

    @Test
    fun `getFileTypes returns correct file types`() {
        val expected = listOf(
            Pair(SnippetLanguage.PRINTSCRIPT, "ps"),
            Pair(SnippetLanguage.JAVA, ".java")
        )
        val result = FileType.getFileTypes()

        assertEquals(expected.size, result.size)
        expected.forEach { expectedPair ->
            val resultPair = result.find { it.first == expectedPair.first }
            assertNotNull(resultPair)
            assertEquals(expectedPair.second, resultPair?.second)
        }
    }

    @Test
    fun `createSnippet should return SnippetDto on successful creation`() {
        val createSnippetInput = CreateSnippet("Test Snippet", "Test Content", SnippetLanguage.PRINTSCRIPT, "ps")
        val userId = "userId"
        val token = "token"
        val snippetStatus = SnippetStatus(ComplianceSnippet.PENDING)

        whenever(userRepository.findByUserId(userId)).thenReturn(UserSnippet(userId, "userName"))
        whenever(snippetStatusRepository.findByStatus(ComplianceSnippet.PENDING)).thenReturn(snippetStatus)
        whenever(snippetRepository.save(any())).thenAnswer { invocation ->
            val snippet = invocation.getArgument(0) as Snippet
            snippet.id = 1L
            snippet
        }

        val result = managerService.createSnippet(createSnippetInput, userId, token)

        assertEquals("Test Snippet", result.name)
        assertEquals("Test Content", result.content)
        assertEquals(ComplianceSnippet.PENDING, result.compliance)
        assertEquals("userName", result.author)
        assertEquals(SnippetLanguage.PRINTSCRIPT.toString(), result.language.toString())
        assertEquals("ps", result.extension)

        verify(userRepository).findByUserId(userId)
        verify(snippetStatusRepository).findByStatus(ComplianceSnippet.PENDING)
        verify(snippetRepository).save(any())
    }

    @Test
    fun `getSnippet should return SnippetDto with correct data`() {
        val snippetId = "1"
        val expectedContent = "Snippet Content"
        val snippet = Snippet(
            "Test Snippet",
            SnippetLanguage.PRINTSCRIPT,
            "ps",
            UserSnippet("user1", "User Name"),
            SnippetStatus(ComplianceSnippet.PENDING)
        ).apply { id = 1L }
        val expectedSnippetDto = SnippetDto(
            id = snippet.id!!,
            name = snippet.name,
            content = expectedContent,
            compliance = snippet.status.status,
            author = snippet.name,
            language = snippet.language,
            extension = snippet.extension
        )

        whenever(snippetRepository.findById(snippetId.toLong())).thenReturn(Optional.of(snippet))
        whenever(bucketAPI.getSnippet(snippetId)).thenReturn(expectedContent)

        val result = managerService.getSnippet(snippetId)

        assertEquals(expectedSnippetDto.name, result.name)
        assertEquals(expectedSnippetDto.content, result.content)
        assertEquals(expectedSnippetDto.compliance, result.compliance)
        assertEquals(expectedSnippetDto.language, result.language)
        assertEquals(expectedSnippetDto.extension, result.extension)
        verify(snippetRepository).findById(snippetId.toLong())
        verify(bucketAPI).getSnippet(snippetId)
    }

    @Test
    fun `shareSnippet should throw BadReqException when user is not the owner`() {
        val userId = "user1"
        val token = "token123"
        val shareSnippetInput = ShareSnippetInput("1", "1")

        `when`(snippetPerm.getPermissionType(shareSnippetInput.snippetId, userId, token)).thenReturn(PermissionType.R)

        val exception = assertThrows<BadReqException> {
            managerService.shareSnippet(userId, shareSnippetInput, token)
        }

        assertEquals("The user has no permissions for updating the snippet", exception.message)
    }

    @Test
    fun `shareSnippet should share the snippet and return SnippetDto when user is the owner`() {
        val userId = "user1"
        val token = "token123"
        val shareSnippetInput = ShareSnippetInput("1", "user2")
        val snippetId = shareSnippetInput.snippetId
        val snippet = Snippet(
            "Test Snippet",
            SnippetLanguage.PRINTSCRIPT,
            "ps",
            UserSnippet("user1", "User Name"),
            SnippetStatus(ComplianceSnippet.PENDING)
        ).apply { id = 1L }
        val expectedSnippetDto = SnippetDto(
            id = snippet.id!!,
            name = snippet.name,
            content = "Snippet Content",
            compliance = snippet.status.status,
            author = snippet.name,
            language = snippet.language,
            extension = snippet.extension
        )

        `when`(snippetPerm.getPermissionType(snippetId, userId, token)).thenReturn(PermissionType.OWNER)
        `when`(snippetRepository.findById(snippetId.toLong())).thenReturn(Optional.of(Snippet("Test Snippet", SnippetLanguage.PRINTSCRIPT, "ps", UserSnippet(userId, "User Name"), SnippetStatus(ComplianceSnippet.PENDING)).apply { id = 1L }))
        `when`(bucketAPI.getSnippet(snippetId)).thenReturn("Snippet Content")


        val result = managerService.shareSnippet(userId, shareSnippetInput, token)

        assertNotNull(result)
        assertEquals(expectedSnippetDto.name, result.name)
        assertEquals(expectedSnippetDto.content, result.content)
        assertEquals(expectedSnippetDto.compliance, result.compliance)
        assertEquals(expectedSnippetDto.language, result.language)
        assertEquals(expectedSnippetDto.extension, result.extension)
    }
}
