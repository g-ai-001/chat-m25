package app.chat_m25.data.repository

import app.chat_m25.data.local.dao.ContactDao
import app.chat_m25.data.local.entity.ContactEntity
import app.chat_m25.domain.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepository @Inject constructor(
    private val contactDao: ContactDao
) {
    fun getAllContacts(): Flow<List<Contact>> {
        return contactDao.getAllContacts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getStarredContacts(): Flow<List<Contact>> {
        return contactDao.getStarredContacts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getContactById(id: Long): Contact? {
        return contactDao.getContactById(id)?.toDomain()
    }

    fun searchContacts(keyword: String): Flow<List<Contact>> {
        return contactDao.searchContacts(keyword).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addContact(name: String, phone: String = "", remark: String = ""): Long {
        val contact = ContactEntity(name = name, phone = phone, remark = remark)
        return contactDao.insertContact(contact)
    }

    suspend fun updateContact(contact: Contact) {
        contactDao.updateContact(contact.toEntity())
    }

    suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact.toEntity())
    }

    private fun ContactEntity.toDomain() = Contact(
        id = id,
        name = name,
        avatar = avatar,
        remark = remark,
        phone = phone,
        isStarred = isStarred
    )

    private fun Contact.toEntity() = ContactEntity(
        id = id,
        name = name,
        avatar = avatar,
        remark = remark,
        phone = phone,
        isStarred = isStarred
    )
}
