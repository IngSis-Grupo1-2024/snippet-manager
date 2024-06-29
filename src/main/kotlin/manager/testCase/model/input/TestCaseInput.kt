package manager.testCase.model.input

data class TestCaseInput(
    val id: String,
    val snippetId: String,
    val name: String,
    val input: List<String>?,
    val output: List<String>?,
    val envVars: String,
)
