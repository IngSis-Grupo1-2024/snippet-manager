package manager.tests.model

import manager.common.entity.CommonEntity
import manager.manager.model.dto.SnippetDto
import manager.manager.model.dto.SnippetListDto
import manager.manager.model.enums.ComplianceSnippet
import manager.manager.model.enums.SnippetLanguage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ManagerModelTests {

    private class TestEntity : CommonEntity()

    @Test
    fun `test getters and setters`() {
        val testEntity = TestEntity().apply {
            id = 1L
            createdAt = LocalDateTime.of(2020, 1, 1, 0, 0)
            updatedAt = LocalDateTime.of(2020, 1, 2, 0, 0)
        }

        assertEquals(1L, testEntity.id)
        assertEquals(LocalDateTime.of(2020, 1, 1, 0, 0), testEntity.createdAt)
        assertEquals(LocalDateTime.of(2020, 1, 2, 0, 0), testEntity.updatedAt)
    }

    @Test
    fun `merge with non-empty lists`() {
        val snippetDto = SnippetDto(
            "Snippet1",
            "content",
            SnippetLanguage.PRINTSCRIPT,
            "ps",
            1,
            ComplianceSnippet.PENDING,
            "1"
        )

        val list1 = SnippetListDto(listOf(snippetDto, snippetDto))
        val list2 = SnippetListDto(listOf(snippetDto))

        val mergedList = list1.merge(list2)

        assertEquals(3, mergedList.snippets.size)
    }

    @Test
    fun `merge with an empty and a non-empty list`() {
        val snippetDto = SnippetDto(
            "Snippet1",
            "content",
            SnippetLanguage.PRINTSCRIPT,
            "ps",
            1,
            ComplianceSnippet.PENDING,
            "1"
        )

        val emptyList = SnippetListDto(emptyList())
        val nonEmptyList = SnippetListDto(listOf(snippetDto))

        val mergedList = emptyList.merge(nonEmptyList)

        assertEquals(1, mergedList.snippets.size)
    }

    @Test
    fun `merge two empty lists`() {
        val emptyList1 = SnippetListDto(emptyList())
        val emptyList2 = SnippetListDto(emptyList())

        val mergedList = emptyList1.merge(emptyList2)

        assertEquals(0, mergedList.snippets.size)
    }

}
