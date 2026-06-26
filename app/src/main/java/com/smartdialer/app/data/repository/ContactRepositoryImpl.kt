package com.smartdialer.app.data.repository

import com.smartdialer.app.data.device.DeviceDataSource
import com.smartdialer.app.data.local.dao.ContactDao
import com.smartdialer.app.data.mapper.EntityMapper.toDomain
import com.smartdialer.app.data.mapper.EntityMapper.toEntity
import com.smartdialer.app.domain.model.Contact
import com.smartdialer.app.domain.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation of [ContactRepository] backed by Room database.
 * Syncs from the device ContactsProvider on demand.
 */
class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao,
    private val deviceDataSource: DeviceDataSource
) : ContactRepository {

    override fun getAllContacts(): Flow<List<Contact>> {
        return contactDao.getAllContacts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFavoriteContacts(): Flow<List<Contact>> {
        return contactDao.getFavoriteContacts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getContactById(id: Long): Contact? {
        return contactDao.getContactById(id)?.toDomain()
    }

    override fun searchContacts(query: String): Flow<List<Contact>> {
        return contactDao.searchContacts(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchByT9(t9Query: String): Flow<List<Contact>> {
        val t9Map = mapOf(
            '2' to "abc", '3' to "def", '4' to "ghi",
            '5' to "jkl", '6' to "mno", '7' to "pqrs",
            '8' to "tuv", '9' to "wxyz"
        )
        return contactDao.getAllContacts().map { entities ->
            entities.filter { entity ->
                matchesT9(entity.name, t9Query, t9Map) ||
                    entity.phoneNumbersJson.contains(t9Query)
            }.map { it.toDomain() }
        }
    }

    private fun matchesT9(name: String, t9Query: String, t9Map: Map<Char, String>): Boolean {
        if (t9Query.isEmpty()) return true
        val nameLower = name.lowercase()
        val words = nameLower.split(" ", "-", "_")
        return words.any { word ->
            if (word.length < t9Query.length) return@any false
            t9Query.indices.all { i ->
                val digit = t9Query[i]
                val chars = t9Map[digit] ?: return@all false
                i < word.length && word[i] in chars
            }
        }
    }

    override suspend fun addContact(contact: Contact): Long {
        return contactDao.insertContact(contact.toEntity())
    }

    override suspend fun updateContact(contact: Contact) {
        contactDao.updateContact(contact.toEntity())
    }

    override suspend fun deleteContact(contactId: Long) {
        contactDao.deleteContact(contactId)
    }

    override suspend fun toggleFavorite(contactId: Long, isFavorite: Boolean) {
        contactDao.updateFavoriteStatus(contactId, isFavorite)
    }

    override fun getContactsByGroup(group: String): Flow<List<Contact>> {
        return contactDao.getContactsByGroup(group).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncFromDevice() {
        withContext(Dispatchers.IO) {
            val deviceContacts = deviceDataSource.getDeviceContacts()
            // Clear old cached contacts and insert fresh from device
            contactDao.deleteAllContacts()
            contactDao.insertContacts(deviceContacts)
        }
    }
}
