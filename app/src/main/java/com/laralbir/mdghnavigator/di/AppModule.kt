package com.laralbir.mdghnavigator.di

import com.laralbir.mdghnavigator.core.common.event.DefaultEventBus
import com.laralbir.mdghnavigator.core.common.event.EventBus
import com.laralbir.mdghnavigator.core.common.util.SelectiveFileFilter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideEventBus(): EventBus = DefaultEventBus()

    @Provides
    @Singleton
    fun provideSelectiveFileFilter(): SelectiveFileFilter = SelectiveFileFilter()
}
