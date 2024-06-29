package manager.redis.producer

import manager.redis.events.LintRequest
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class LintProducer
    @Autowired
    constructor(
        @Value("\${manager.redis.stream.request_lint_key}") streamKey: String,
        redis: RedisTemplate<String, String>
    ) : RedisStreamProducer(streamKey, redis) {
        suspend fun publishEvent(event: LintRequest) {
            println("Publishing event: $event")
            emit(event)
        }
    }
