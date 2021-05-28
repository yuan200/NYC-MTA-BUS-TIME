package com.wen.android.mtabuscomparison.injection

import com.wen.android.mtabuscomparison.util.coroutine.DefaultDispatcherProvider
import com.wen.android.mtabuscomparison.util.coroutine.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class CoroutineModule {

    @Binds
    abstract fun dispatcherProvider(defaultDispatcherProvider: DefaultDispatcherProvider): DispatcherProvider

}