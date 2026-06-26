package com.smartdialer.app.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SmartDialerApi {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): TokenResponse

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): UserResponse

    @POST("sync/")
    suspend fun syncData(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    ): SyncResponse
}

// Request/Response models

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val password: String, val display_name: String? = null)
data class TokenResponse(val access_token: String, val token_type: String)
data class UserResponse(val id: Int, val email: String, val display_name: String?)

data class SyncRequest(
    val contacts: List<ContactBackupDto>,
    val call_logs: List<CallLogBackupDto>,
    val notes: List<NoteBackupDto>
)

data class SyncResponse(
    val contacts_synced: Int,
    val call_logs_synced: Int,
    val notes_synced: Int
)

data class ContactBackupDto(
    val name: String,
    val nickname: String?,
    val phone_numbers_json: String,
    val emails_json: String,
    val address: String?,
    val company: String?,
    val photo_uri: String?,
    val is_favorite: Boolean,
    val group_name: String?,
    val notes: String?,
    val birthday: String?,
    val device_contact_id: String?,
    val created_at: Long,
    val updated_at: Long
)

data class CallLogBackupDto(
    val contact_name: String?,
    val number: String,
    val formatted_number: String?,
    val call_type: String,
    val duration: Long,
    val timestamp: Long,
    val sim_slot: Int
)

data class NoteBackupDto(
    val contact_id: Long?,
    val content: String,
    val call_summary: String?,
    val ai_tags: String?,
    val created_at: Long,
    val updated_at: Long
)
