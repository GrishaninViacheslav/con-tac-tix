package io.github.grishaninvyacheslav.con_tac_tix.presenters.main

import io.github.grishaninvyacheslav.con_tac_tix.App
import io.github.grishaninvyacheslav.con_tac_tix.ui.screens.IScreens
import io.github.grishaninvyacheslav.con_tac_tix.ui.screens.Screens
import moxy.MvpPresenter

class MainPresenter() : MvpPresenter<MainView>() {
    var router = App.instance.router

    var screens: IScreens = Screens()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        router.replaceScreen(screens.game())
    }

    fun backClicked() {
        router.exit()
    }
}