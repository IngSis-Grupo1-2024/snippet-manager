package manager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SnippetManagerApplication

fun main(args: Array<String>) {
    runApplication<SnippetManagerApplication>()

//    val azuriteBucketV1 = System.getenv("AZURITE_BUCKET_V1")
//    val restTemplate = RestTemplate()
//    val bucketManager = RemoteBucket(azuriteBucketV1, restTemplate)

//    val snippet = CreateSnippet("basic-snippet", "println('Hello World')", SnippetLanguage.PRINTSCRIPT, "1.0.0")
//    val response = bucketManager.createSnippet("1", snippet.content)!!
//    val response = bucketManager.getSnippet("1")
//    val response = bucketManager.deleteSnippet("1")

//    println(response.statusCode)
//    println(response.headers)
//    println(response.body)

//    val manager.runner = RunnerManager(restTemplate, "http://localhost:8084")

//    val snippetInfo = SnippetInfo(content = response, extension = ".ps", input = listOf(), language = SnippetLanguage.PRINTSCRIPT, name = "basic-snippet", version = "v1")

//    val runningOutput = manager.runner.runSnippet(snippetInfo)

}