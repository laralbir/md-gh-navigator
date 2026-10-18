package com.laralbir.mdghnavigator.feature.tts.di

import com.laralbir.mdghnavigator.feature.tts.AndroidTtsSpeechEngine
import com.laralbir.mdghnavigator.feature.tts.TtsSpeechEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TtsModule {

    @Binds
    @Singleton
    abstract fun bindTtsSpeechEngine(
        impl: AndroidTtsSpeechEngine
    ): TtsSpeechEngine
}
