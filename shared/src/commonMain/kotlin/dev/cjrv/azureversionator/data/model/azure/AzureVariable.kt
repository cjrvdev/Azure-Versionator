package dev.cjrv.azureversionator.data.model.azure

import kotlinx.serialization.Serializable

@Serializable
data class AzureVariable(
    var name: String,
    var value: String,
    var isSecret: Boolean = false,
    var textFieldType: TextFieldType = TextFieldType.SingleLine,
    var isRequired: Boolean = true
)

@Serializable
enum class TextFieldType {
    SingleLine,
    Multiline
}
