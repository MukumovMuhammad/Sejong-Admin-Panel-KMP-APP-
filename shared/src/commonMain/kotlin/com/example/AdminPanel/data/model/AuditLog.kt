package com.example.AdminPanel.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class AuditLog(
    val id: String,
    val admin_user: String,
    val action: String,
    val model_name: String,
    val object_id: String? = null,
    val changes: JsonElement,
    val timestamp: String
)



data class ChangeUiItem(
    val fieldName: String,
    val description: String
)

data class VisualAuditLog(
    val rawLog: AuditLog,
    val changesDisplay: List<ChangeUiItem>
)

fun AuditLog.getChangesList(): List<ChangeUiItem> {
    val uiList = mutableListOf<ChangeUiItem>()

    try {
        val jsonObject = this.changes.jsonObject

        if (this.action == "update" && jsonObject.containsKey("updated_fields")) {
            // Context: Parse array format ["email", "date_of_birth"]
            jsonObject["updated_fields"]?.jsonArray?.forEach { element ->
                val field = element.jsonPrimitive.content
                uiList.add(ChangeUiItem(fieldName = field, description = "Field updated"))
            }
        } else {
            // Context: Parse standard key-value map {"username": "Maximus", "status": "Student"}
            jsonObject.forEach { (key, jsonElement) ->
                val value = jsonElement.jsonPrimitive.content
                uiList.add(ChangeUiItem(fieldName = key, description = "Value: $value"))
            }
        }
    } catch (e: Exception) {
        uiList.add(ChangeUiItem(fieldName = "Error", description = "Could not parse changes layout"))
    }

    return uiList
}

@Serializable
data class AuditLogResponse(
    val total: Int = 0,
    val offset: Int = 0,
    val limit: Int = 50,
    val has_more: Boolean = false,
    val logs: List<AuditLog> = emptyList()
)
