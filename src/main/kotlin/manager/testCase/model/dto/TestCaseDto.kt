package com.example.demo.testCase.model.dto

import manager.common.rest.dto.Output

data class TestCaseDto(
    val id: String,
    val name: String,
    val input: List<String>?,
    val output: List<String>?,
    val envVars: String,
) : Output
