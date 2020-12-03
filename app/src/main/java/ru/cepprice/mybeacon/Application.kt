package ru.cepprice.mybeacon

import android.app.Application
import ru.cepprice.mybeacon.di.AppComponent
import ru.cepprice.mybeacon.di.ContextModule
import ru.cepprice.mybeacon.di.DaggerAppComponent

class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()

        appComponent.inject(this)
    }
}