package dev.cjrv.azureversionator.data.model.app

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class Profile(
    val id: Uuid = Uuid.random(),
    val name: String
)
