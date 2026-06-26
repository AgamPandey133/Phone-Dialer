from sqlalchemy import Column, Integer, String, Boolean, BigInteger, Text, ForeignKey
from sqlalchemy.orm import relationship
from app.core.database import Base


class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    email = Column(String(255), unique=True, index=True, nullable=False)
    hashed_password = Column(String(255), nullable=False)
    display_name = Column(String(255), nullable=True)
    device_id = Column(String(255), nullable=True)
    created_at = Column(BigInteger, nullable=False)
    updated_at = Column(BigInteger, nullable=False)

    contacts = relationship("ContactBackup", back_populates="user", cascade="all, delete-orphan")
    call_logs = relationship("CallLogBackup", back_populates="user", cascade="all, delete-orphan")
    notes = relationship("NoteBackup", back_populates="user", cascade="all, delete-orphan")


class ContactBackup(Base):
    __tablename__ = "contacts_backup"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    name = Column(String(255), nullable=False)
    nickname = Column(String(255), nullable=True)
    phone_numbers_json = Column(Text, default="[]")
    emails_json = Column(Text, default="[]")
    address = Column(Text, nullable=True)
    company = Column(String(255), nullable=True)
    photo_uri = Column(Text, nullable=True)
    is_favorite = Column(Boolean, default=False)
    group_name = Column(String(255), nullable=True)
    notes = Column(Text, nullable=True)
    birthday = Column(String(50), nullable=True)
    device_contact_id = Column(String(255), nullable=True)
    created_at = Column(BigInteger, nullable=False)
    updated_at = Column(BigInteger, nullable=False)

    user = relationship("User", back_populates="contacts")


class CallLogBackup(Base):
    __tablename__ = "call_logs_backup"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    contact_name = Column(String(255), nullable=True)
    number = Column(String(50), nullable=False)
    formatted_number = Column(String(50), nullable=True)
    call_type = Column(String(20), nullable=False)
    duration = Column(BigInteger, default=0)
    timestamp = Column(BigInteger, nullable=False)
    sim_slot = Column(Integer, default=0)

    user = relationship("User", back_populates="call_logs")


class NoteBackup(Base):
    __tablename__ = "notes_backup"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    contact_id = Column(Integer, nullable=True)
    content = Column(Text, nullable=False)
    call_summary = Column(Text, nullable=True)
    ai_tags = Column(Text, nullable=True)
    created_at = Column(BigInteger, nullable=False)
    updated_at = Column(BigInteger, nullable=False)

    user = relationship("User", back_populates="notes")
