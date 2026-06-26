from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import delete
from typing import Any

from app.core.database import get_db
from app.core.security import get_current_user
from app.models.user import User, ContactBackup, CallLogBackup, NoteBackup
from app.schemas.schemas import SyncRequest, SyncResponse

router = APIRouter()

@router.post("/", response_model=SyncResponse)
async def sync_data(
    sync_data: SyncRequest,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
) -> Any:
    """
    Sync all user data from device to cloud.
    This overwrites the existing cloud backup with the device's current state for simplicity.
    A more robust version would handle granular diffs and conflict resolution.
    """
    # 1. Clear existing backup data for this user
    await db.execute(delete(ContactBackup).where(ContactBackup.user_id == current_user.id))
    await db.execute(delete(CallLogBackup).where(CallLogBackup.user_id == current_user.id))
    await db.execute(delete(NoteBackup).where(NoteBackup.user_id == current_user.id))
    
    # 2. Insert new Contacts
    contacts_to_insert = [
        ContactBackup(user_id=current_user.id, **contact.model_dump())
        for contact in sync_data.contacts
    ]
    db.add_all(contacts_to_insert)
    
    # 3. Insert new Call Logs
    call_logs_to_insert = [
        CallLogBackup(user_id=current_user.id, **log.model_dump())
        for log in sync_data.call_logs
    ]
    db.add_all(call_logs_to_insert)
    
    # 4. Insert new Notes
    notes_to_insert = [
        NoteBackup(user_id=current_user.id, **note.model_dump())
        for note in sync_data.notes
    ]
    db.add_all(notes_to_insert)
    
    await db.commit()
    
    return SyncResponse(
        contacts_synced=len(contacts_to_insert),
        call_logs_synced=len(call_logs_to_insert),
        notes_synced=len(notes_to_insert)
    )
