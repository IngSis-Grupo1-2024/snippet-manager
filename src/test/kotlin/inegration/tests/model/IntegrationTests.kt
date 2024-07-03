package inegration.tests.model

import manager.manager.integration.permission.dto.SnippetIds
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class IntegrationTests {

    @Test
    fun `snippets property returns correct list of Long values`() {
        val snippetList = listOf(1L, 2L, 3L)
        val snippetIds = SnippetIds(snippetList)

        assertEquals(snippetList, snippetIds.snippets)
    }

    @Test
    fun `equals returns true for instances with the same snippets list`() {
        val snippetList = listOf(1L, 2L, 3L)
        val snippetIds1 = SnippetIds(snippetList)
        val snippetIds2 = SnippetIds(snippetList)

        assertTrue(snippetIds1 == snippetIds2)
    }

    @Test
    fun `hashCode is the same for instances with the same snippets list`() {
        val snippetList = listOf(1L, 2L, 3L)
        val snippetIds1 = SnippetIds(snippetList)
        val snippetIds2 = SnippetIds(snippetList)

        assertEquals(snippetIds1.hashCode(), snippetIds2.hashCode())
    }
}
