package io.github.grishaninvyacheslav.con_tac_tix.ui.screens

import com.github.terrakok.cicerone.androidx.FragmentScreen
import io.github.grishaninvyacheslav.con_tac_tix.ui.fragments.GameFragment
import io.github.grishaninvyacheslav.con_tac_tix.ui.fragments.GamesHistoryFragment

class Screens : IScreens {
    override fun game() = FragmentScreen { GameFragment.newInstance() }
    override fun history() = FragmentScreen { GamesHistoryFragment.newInstance() }
}