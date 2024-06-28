package manager.manager.model.dto

import manager.common.rest.dto.Output

class FileTypeDto(
    val language: String,
    val extension: String,
) : Output
