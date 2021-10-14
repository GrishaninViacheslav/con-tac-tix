package io.github.grishaninvyacheslav.con_tac_tix

import android.app.Application
import com.github.terrakok.cicerone.Cicerone


class App : Application() {
    private val cicerone = Cicerone.create()
    val router get() = cicerone.router
    val navigatorHolder get() = cicerone.getNavigatorHolder()

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object {
        private var INSTANCE: App? = null
        val instance: App
            get() = INSTANCE!!
    }
}