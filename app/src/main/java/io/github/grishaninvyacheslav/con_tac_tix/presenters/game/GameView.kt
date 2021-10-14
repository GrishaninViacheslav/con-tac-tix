package io.github.grishaninvyacheslav.con_tac_tix.presenters.game

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndStrategy::class)
interface GameView: MvpView{
    fun init()
    fun showTurn(position: Pair<Int, Int>, turnOwner: Owner)
    fun cancelTurn(position: Pair<Int, Int>)
    fun showGameOverPath(positions: List<Pair<Int, Int>>)
    fun cancelGameOverMessage(positions: List<Pair<Int, Int>>)
    fun showHistoryEditor()
    fun updateHistory()
    fun closeHistoryEditor()
}