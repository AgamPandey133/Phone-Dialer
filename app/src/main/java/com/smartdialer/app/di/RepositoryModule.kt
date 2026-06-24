package com.smartdialer.app.di

import com.smartdialer.app.data.repository.ContactRepositoryImpl
import com.smartdialer.app.data.repository.CallLogRepositoryImpl
import com.smartdialer.app.domain.repository.ContactRepository
import com.smartdialer.app.domain.repository.CallLogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module binding repository interfaces to their implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindContactRepository(impl: ContactRepositoryImpl): ContactRepository

    @Binds
    @Singleton
    abstract fun bindCallLogRepository(impl: CallLogRepositoryImpl): CallLogRepository
}
