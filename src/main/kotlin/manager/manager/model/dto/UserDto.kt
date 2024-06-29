package manager.manager.model.dto

import manager.common.rest.dto.Output

data class UserDto(
    val name: String,
    val id: String,
) : Output
