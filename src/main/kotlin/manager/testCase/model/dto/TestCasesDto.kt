package com.example.demo.testCase.model.dto

import manager.common.rest.dto.Output

data class TestCasesDto(
    val testCases: List<TestCaseDto>,
) : Output
