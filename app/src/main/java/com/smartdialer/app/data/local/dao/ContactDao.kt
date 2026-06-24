package com.smartdialer.app.data.local.dao

import androidx.room.*
import com.smartdialer.app.data.local.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Contact operations.
 */
@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts WHERE isHidden = 0 ORDER BY name ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE isFavorite = 1 AND isHidden = 0 ORDER BY name ASC")
    fun getFavoriteContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Long): ContactEntity?

    @Query("""
        SELECT * FROM contacts 
        WHERE isHidden = 0 AND (
            name LIKE '%' || :query || '%' 
            OR nickname LIKE '%' || :query || '%'
            OR company LIKE '%' || :query || '%'
            OR phoneNumbersJson LIKE '%' || :query || '%'
        )
        ORDER BY name ASC
    """)
    fun searchContacts(query: String): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE isHidden = 0 AND groupName = :group ORDER BY name ASC")
    fun getContactsByGroup(group: String): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Query("UPDATE contacts SET isFavorite = :isFavorite, updatedAt = :timestamp WHERE id = :contactId")
    suspend fun updateFavoriteStatus(contactId: Long, isFavorite: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteContact(id: Long)

    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()

    @Query("SELECT COUNT(*) FROM contacts WHERE isHidden = 0")
    suspend fun getContactCount(): Int
}
