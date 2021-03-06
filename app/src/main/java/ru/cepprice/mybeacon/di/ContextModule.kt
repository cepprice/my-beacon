package ru.cepprice.mybeacon.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContextModule(private val context: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context = context
}