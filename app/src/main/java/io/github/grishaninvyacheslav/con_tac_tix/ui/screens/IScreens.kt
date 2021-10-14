package io.github.grishaninvyacheslav.con_tac_tix.ui.screens

import com.github.terrakok.cicerone.Screen

interface IScreens {
    fun game(): Screen
    fun history(): Screen
}