package manager.rules.model.input

import com.nimbusds.jose.shaded.gson.JsonObject

class ConfigInput{
    companion object{
        fun getJson(userId: String, version: String): JsonObject{
            val json = JsonObject()
            json.addProperty("userId", userId)
            json.addProperty("version", version)
            return json
        }
    }
}