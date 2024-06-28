package com.example.demo.redis.producer

import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import manager.redis.producer.LintRequest

@Component
@Profile("!test")
class LintProducer
    @Autowired
    constructor(
        @Value("\${manager.redis.stream.request_lint_key}") streamKey: String,
        redis: RedisTemplate<String, String>,
    ) : RedisStreamProducer(streamKey, redis) {
        suspend fun publishEvent(event: LintRequest) {
            println("Publishing event: $event")
            emit(event)
        }
    }
