package manager.bucket

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class BucketConfig
@Autowired
constructor(
    private val rest: RestTemplate,
    @Value("\${azuriteBucket}")
    private var urlForBucketV1: String,
) {
    @Bean
    fun createRemoteBucketApi(): BucketManager {
        return BucketManager(urlForBucketV1, rest)
    }
}