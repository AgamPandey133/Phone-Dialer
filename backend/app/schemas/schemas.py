from pydantic import BaseModel, EmailStr
from typing import Optional


# ---- Auth ----

class UserCreate(BaseModel):
    email: EmailStr
    password: str
    display_name: Optional[str] = None

class UserLogin(BaseModel):
    email: EmailStr
    password: str

class UserResponse(BaseModel):
    id: int
    email: str
    display_name: Optional[str] = None
    model_config = {"from_attributes": True}

class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"


# ---- Contact Backup ----

class ContactBackupSchema(BaseModel):
    name: str
    nickname: Optional[str] = None
    phone_numbers_json: str = "[]"
    emails_json: str = "[]"
    address: Optional[str] = None
    company: Optional[str] = None
    photo_uri: Optional[str] = None
    is_favorite: bool = False
    group_name: Optional[str] = None
    notes: Optional[str] = None
    birthday: Optional[str] = None
    device_contact_id: Optional[str] = None
    created_at: int
    updated_at: int

class ContactBackupResponse(ContactBackupSchema):
    id: int
    model_config = {"from_attributes": True}


# ---- Call Log Backup ----

class CallLogBackupSchema(BaseModel):
    contact_name: Optional[str] = None
    number: str
    formatted_number: Optional[str] = None
    call_type: str
    duration: int = 0
    timestamp: int
    sim_slot: int = 0

class CallLogBackupResponse(CallLogBackupSchema):
    id: int
    model_config = {"from_attributes": True}


# ---- Note Backup ----

class NoteBackupSchema(BaseModel):
    contact_id: Optional[int] = None
    content: str
    call_summary: Optional[str] = None
    ai_tags: Optional[str] = None
    created_at: int
    updated_at: int

class NoteBackupResponse(NoteBackupSchema):
    id: int
    model_config = {"from_attributes": True}


# ---- Sync ----

class SyncRequest(BaseModel):
    contacts: list[ContactBackupSchema] = []
    call_logs: list[CallLogBackupSchema] = []
    notes: list[NoteBackupSchema] = []

class SyncResponse(BaseModel):
    contacts_synced: int
    call_logs_synced: int
    notes_synced: int
