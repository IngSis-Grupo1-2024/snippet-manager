package manager.manager.model.dto

import manager.common.rest.dto.Output

data class UsersDto(
    val users: List<UserDto>,
) : Output
