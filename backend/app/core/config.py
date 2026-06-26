# Smart Dialer Backend

from pydantic_settings import BaseSettings
from functools import lru_cache


class Settings(BaseSettings):
    """Application configuration loaded from environment variables."""
    
    # App
    APP_NAME: str = "Smart Dialer API"
    DEBUG: bool = False
    API_V1_PREFIX: str = "/api/v1"
    
    # Database (Neon PostgreSQL)
    DATABASE_URL: str = "postgresql+asyncpg://user:password@localhost:5432/smartdialer"
    
    # JWT Auth
    JWT_SECRET_KEY: str = "change-this-to-a-secure-random-key"
    JWT_ALGORITHM: str = "HS256"
    JWT_ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24 * 7  # 7 days
    
    # CORS
    ALLOWED_ORIGINS: list[str] = ["*"]

    model_config = {
        "env_file": ".env",
        "env_file_encoding": "utf-8",
        "case_sensitive": True,
    }


@lru_cache
def get_settings() -> Settings:
    return Settings()
