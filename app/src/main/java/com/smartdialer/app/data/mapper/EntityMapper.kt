package com.smartdialer.app.data.mapper

import com.smartdialer.app.data.local.entity.CallLogEntity
import com.smartdialer.app.data.local.entity.ContactEntity
import com.smartdialer.app.domain.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * Mappers between Room entities and domain models.
 * Keeps the data and domain layers cleanly separated.
 */
object EntityMapper {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val phoneNumberListType = Types.newParameterizedType(List::class.java, Map::class.java)
    private val stringListType = Types.newParameterizedType(List::class.java, String::class.java)
    private val stringListAdapter = moshi.adapter<List<String>>(stringListType)

    // ---- Contact Mapping ----

    fun ContactEntity.toDomain(): Contact {
        val phoneNumbers = try {
            val listType = Types.newParameterizedType(
                List::class.java,
                Map::class.java
            )
            val adapter = moshi.adapter<List<Map<String, String>>>(listType)
            adapter.fromJson(phoneNumbersJson)?.map { map ->
                PhoneNumber(
                    number = map["number"] ?: "",
                    label = try {
                        PhoneNumberLabel.valueOf(map["label"] ?: "MOBILE")
                    } catch (e: Exception) {
                        PhoneNumberLabel.MOBILE
                    }
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        val emails = try {
            stringListAdapter.fromJson(emailsJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        return Contact(
            id = id,
            name = name,
            nickname = nickname,
            phoneNumbers = phoneNumbers,
            emails = emails,
            address = address,
            company = company,
            photoUri = photoUri,
            isFavorite = isFavorite,
            isHidden = isHidden,
            group = groupName,
            notes = notes,
            birthday = birthday,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun Contact.toEntity(): ContactEntity {
        val phoneNumbersJson = try {
            val listData = phoneNumbers.map { mapOf("number" to it.number, "label" to it.label.name) }
            val listType = Types.newParameterizedType(List::class.java, Map::class.java)
            val adapter = moshi.adapter<List<Map<String, String>>>(listType)
            adapter.toJson(listData)
        } catch (e: Exception) {
            "[]"
        }

        val emailsJson = try {
            stringListAdapter.toJson(emails)
        } catch (e: Exception) {
            "[]"
        }

        return ContactEntity(
            id = id,
            name = name,
            nickname = nickname,
            phoneNumbersJson = phoneNumbersJson,
            emailsJson = emailsJson,
            address = address,
            company = company,
            photoUri = photoUri,
            isFavorite = isFavorite,
            isHidden = isHidden,
            groupName = group,
            notes = notes,
            birthday = birthday,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    // ---- CallLog Mapping ----

    fun CallLogEntity.toDomain(): CallLogEntry {
        return CallLogEntry(
            id = id,
            contactId = contactId,
            contactName = contactName,
            number = number,
            formattedNumber = formattedNumber,
            type = try { CallType.valueOf(type) } catch (e: Exception) { CallType.INCOMING },
            duration = duration,
            timestamp = timestamp,
            isRead = isRead,
            simSlot = simSlot,
            location = location,
            photoUri = photoUri
        )
    }

    fun CallLogEntry.toEntity(): CallLogEntity {
        return CallLogEntity(
            id = id,
            contactId = contactId,
            contactName = contactName,
            number = number,
            formattedNumber = formattedNumber,
            type = type.name,
            duration = duration,
            timestamp = timestamp,
            isRead = isRead,
            simSlot = simSlot,
            location = location,
            photoUri = photoUri
        )
    }
}
