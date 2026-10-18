package com.laralbir.mdghnavigator.feature.settings.di

import com.laralbir.mdghnavigator.feature.settings.storage.AndroidStorageManager
import com.laralbir.mdghnavigator.feature.settings.storage.StorageManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun bindStorageManager(
        impl: AndroidStorageManager
    ): StorageManager
}
