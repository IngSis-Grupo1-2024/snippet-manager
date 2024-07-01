package manager.common.bucket

interface BucketAPI {
    fun createSnippet(
        snippetId: String,
        bodyContent: String,
    ): String?

    fun getSnippet(snippetId: String): String

    fun deleteSnippet(snippetId: String)
}
