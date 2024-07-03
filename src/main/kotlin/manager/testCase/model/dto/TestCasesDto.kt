package manager.testCase.model.dto

import manager.common.rest.dto.Output
import manager.testCase.model.dto.TestCaseDto

data class TestCasesDto(
    val testCases: List<TestCaseDto>,
) : Output
