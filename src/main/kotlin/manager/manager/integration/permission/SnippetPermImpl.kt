package manager.manager.integration.permission

import com.nimbusds.jose.shaded.gson.JsonObject
import manager.common.rest.BasicRest
import manager.manager.integration.permission.dto.SnippetIds
import manager.manager.model.dto.AddPermDto
import manager.manager.model.enums.PermissionType
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

class SnippetPermImpl(
    private val restTemplate: RestTemplate,
    private val snippetPermUrl: String
): SnippetPerm {
    override fun addPermission(
        addPermDto: AddPermDto,
        token: String
    ){
        val url = "$snippetPermUrl/map_permission"
        val headers = BasicRest.getAuthHeaders(token)
        headers.contentType = MediaType.APPLICATION_JSON;

        val request = HttpEntity<String>(getJson(addPermDto), headers)
        restTemplate.postForEntity<String>(url, request)
    }

    override fun getPermissionType(snippetId: String, userId: String, token: String): PermissionType {
        val url = "$snippetPermUrl/get_permission_type/$snippetId/$userId"
        val headers = BasicRest.getAuthHeaders(token)
        val request = HttpEntity<String>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, request, PermissionType::class.java)
        return response.body!!
    }

    override fun getSharedSnippets(userId: String, token: String): List<Long> {
        val url = "$snippetPermUrl/shared"
        val headers = BasicRest.getAuthHeaders(token)
        val request = HttpEntity<String>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, request, SnippetIds::class.java)
        return response.body!!.snippets
    }

    private fun getJson(addPermDto: AddPermDto): String {
        val json = JsonObject()
        json.addProperty("permissionType", addPermDto.permissionType.toString())
        json.addProperty("snippetId", addPermDto.snippetId)
        json.addProperty("userId", addPermDto.userId)
        json.addProperty("sharerId", addPermDto.sharerId)
        return json.toString()
    }
}