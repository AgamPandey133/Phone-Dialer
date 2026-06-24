package com.smartdialer.app.data.repository

import com.smartdialer.app.data.local.dao.ContactDao
import com.smartdialer.app.data.mapper.EntityMapper.toDomain
import com.smartdialer.app.data.mapper.EntityMapper.toEntity
import com.smartdialer.app.domain.model.Contact
import com.smartdialer.app.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [ContactRepository] backed by Room database.
 * Device sync (via ContentProvider) will be added in Phase 2.
 */
class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao
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
        // T9 mapping: 2=ABC, 3=DEF, 4=GHI, 5=JKL, 6=MNO, 7=PQRS, 8=TUV, 9=WXYZ
        val t9Map = mapOf(
            '2' to "[abcABC]", '3' to "[defDEF]", '4' to "[ghiGHI]",
            '5' to "[jklJKL]", '6' to "[mnoMNO]", '7' to "[pqrsPQRS]",
            '8' to "[tuvTUV]", '9' to "[wxyzWXYZ]"
        )
        // Build a regex-like pattern for LIKE queries
        // Since SQLite doesn't support regex natively in LIKE, we do T9 matching in-memory
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

        // Check if any word in the name matches the T9 sequence
        val words = nameLower.split(" ", "-", "_")
        return words.any { word ->
            if (word.length < t9Query.length) return@any false
            t9Query.indices.all { i ->
                val digit = t9Query[i]
                val charSet = t9Map[digit] ?: return@all false
                i < word.length && word[i].toString().matches(Regex(charSet))
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
        // TODO: Phase 2 - Read from device ContactsProvider using ContentResolver
    }
}
