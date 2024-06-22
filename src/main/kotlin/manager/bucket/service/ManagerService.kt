package manager.bucket.service

import manager.bucket.integration.BucketAPI
import manager.rules.integration.configuration.SnippetConf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ManagerService
    @Autowired
    constructor(
        private val bucketAPI: BucketAPI
    ): ManagerServiceSpec {
    override fun createSnippet(snippetContent: String): String? {
        return bucketAPI.createSnippet("1", snippetContent)
    }

    override fun getSnippet(snippetId: String): String {
        return bucketAPI.getSnippet(snippetId)
    }

    override fun deleteSnippet(snippetId: String) {
        bucketAPI.deleteSnippet(snippetId)
    }
}