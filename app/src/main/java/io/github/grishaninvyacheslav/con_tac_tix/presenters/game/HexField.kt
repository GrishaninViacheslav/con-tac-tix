package io.github.grishaninvyacheslav.con_tac_tix.presenters.game

import java.util.ArrayDeque

interface HexField{
    val gridSize: Int
    var fieldState: HashMap<Pair<Int, Int>, Pair<CellState, Pair<Int, Int>?>>
    val gameHistory: ArrayDeque<Pair<HashMap<Pair<Int, Int>, Pair<CellState, Pair<Int, Int>?>>, Pair<Int, Int>>>

    fun showTurn(position: Pair<Int, Int>, currTurnOwner:Owner)
    fun cancelTurn(position: Pair<Int, Int>)
    fun showGameOver()
    fun cancelGameOver()
}