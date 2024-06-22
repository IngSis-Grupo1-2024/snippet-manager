package com.example.snippetmanager

import bucket.BucketConfig
import bucket.BucketManager
import com.example.snippetmanager.snippet.CreateSnippet
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate

@SpringBootApplication
class SnippetManagerApplication

fun main(args: Array<String>) {
    runApplication<SnippetManagerApplication>()

//    val azuriteBucketV1 = System.getenv("AZURITE_BUCKET_V1")
//    val restTemplate = RestTemplate()
//    val bucketManager = BucketManager(azuriteBucketV1, restTemplate)

//    val snippet = CreateSnippet("basic-snippet", "println('Hello World')", "ps", "1.0.0")
//    val response = bucketManager.createSnippet("1", snippet.content)
//    val response = bucketManager.getSnippet("1")
//    val response = bucketManager.deleteSnippet("1")
//
//    println(response.statusCode)
//    println(response.headers)
//    println(response.body)

}