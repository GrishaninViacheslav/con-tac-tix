package io.github.grishaninvyacheslav.con_tac_tix.presenters.history

import com.github.terrakok.cicerone.Router
import io.github.grishaninvyacheslav.con_tac_tix.App
import moxy.MvpPresenter

class HistoryPresenter(private val router: Router = App.instance.router) : MvpPresenter<HistoryView>() {
    fun backPressed(): Boolean {
        router.exit()
        return true
    }
}