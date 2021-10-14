package io.github.grishaninvyacheslav.con_tac_tix.presenters.game

import android.util.Log
import com.github.terrakok.cicerone.Router
import io.github.grishaninvyacheslav.con_tac_tix.App
import io.github.grishaninvyacheslav.con_tac_tix.R
import io.github.grishaninvyacheslav.con_tac_tix.presenters.history.HistoryItemView
import io.github.grishaninvyacheslav.con_tac_tix.presenters.history.IHistoryListPresenter
import io.github.grishaninvyacheslav.con_tac_tix.ui.screens.IScreens
import io.github.grishaninvyacheslav.con_tac_tix.ui.screens.Screens
import moxy.MvpPresenter
import java.util.*
import kotlin.collections.HashMap

class GamePresenter(
    private val router: Router = App.instance.router,
    private var screens: IScreens = Screens()
) : MvpPresenter<GameView>() {
    private val hexGame = object : HexField {
        override val gridSize = 11
        override var fieldState =
            hashMapOf<Pair<Int, Int>, Pair<CellState, Pair<Int, Int>?>>().apply {
                for (i in 0 until gridSize) {
                    for (j in 0 until gridSize) {
                        this[Pair(j, i)] = Pair(CellState.FREE, null)
                    }
                }
            }
        override val gameHistory =
            ArrayDeque<Pair<HashMap<Pair<Int, Int>, Pair<CellState, Pair<Int, Int>?>>, Pair<Int, Int>>>()

        override fun showTurn(position: Pair<Int, Int>, currTurnOwner: Owner) {
            viewState.showTurn(position, currTurnOwner)
        }

        override fun cancelTurn(position: Pair<Int, Int>) {
            viewState.cancelTurn(position)
        }

        override fun showGameOver() {
            viewState.showGameOverPath(gameOverPathPresenter.getWinPath())
        }

        override fun cancelGameOver() {
            viewState.cancelGameOverMessage(gameOverPathPresenter.lastWinPath)
        }
    }

    private val gameFlowPresenter = GameFlowPresenter(hexGame)
    private val gameOverPathPresenter: GameOverPathPresenter = GameOverPathPresenter(hexGame)

    inner class HistoryListPresenter : IHistoryListPresenter {
        private val xAxisNumbers = App.instance.resources.getStringArray(R.array.x_axis_numbers)

        val currGameHistory = mutableListOf<Pair<Int, Int>>()

        override var itemClickListener: ((HistoryItemView) -> Unit)? = null

        override fun getCount() = currGameHistory.size

        override fun bindView(view: HistoryItemView) {
            with(currGameHistory[view.pos]) {
                view.setTurnPosition(
                    String.format(
                        App.instance.getString(R.string.position),
                        xAxisNumbers[first],
                        second + 1
                    )
                )
                if(view.pos <= currTurnIndex){
                    if(view.pos % 2 == 0){
                        view.setColor(App.instance.getColor(R.color.blue_0))
                    } else {
                        view.setColor(App.instance.getColor(R.color.red_0))
                    }
                }
                else{
                    view.setColor(App.instance.getColor(R.color.light_gray))
                }
            }
        }
    }

    val historyListPresenter: HistoryListPresenter = HistoryListPresenter()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun makeTurn(position: Pair<Int, Int>) {
        if (isHistoryEditActive) {
            endHistoryEdit()
        }
        gameFlowPresenter.makeTurn(position)
    }

    fun undoTurn() {
        gameFlowPresenter.undoTurn()
    }

    var historyEntries = arrayOf<Any>()
    var currTurnIndex = 0
    var isHistoryEditActive = false

    fun editHistory() {
        Log.d("[MYLOG]", "hexGame.gameHistory.size: ${hexGame.gameHistory.size}")
        isHistoryEditActive = true
        historyListPresenter.currGameHistory.clear()
        historyEntries = hexGame.gameHistory.toArray().reversed().toTypedArray()
        currTurnIndex = hexGame.gameHistory.size - 1
        for (historyEntry in historyEntries) {
            val currEntry =
                (historyEntry as Pair<HashMap<Pair<Int, Int>, Pair<CellState, Pair<Int, Int>?>>, Pair<Int, Int>>).second
            historyListPresenter.currGameHistory.add(Pair(currEntry.second, currEntry.first))
        }
        viewState.updateHistory()
        viewState.showHistoryEditor()
    }

    fun endHistoryEdit() {
        isHistoryEditActive = false
        viewState.closeHistoryEditor()
    }

    fun showGameBoardAtTurnIndex(turnIndex: Int) {
        if (!isHistoryEditActive) {
            return
        }
        if (turnIndex < currTurnIndex) {
            for (i in turnIndex until currTurnIndex) {
                undoTurn()
            }
        } else if (turnIndex > currTurnIndex) {
            for (i in currTurnIndex..turnIndex) {
                gameFlowPresenter.makeTurn((historyEntries[i] as Pair<HashMap<Pair<Int, Int>, Pair<CellState, Pair<Int, Int>?>>, Pair<Int, Int>>).second)
            }
        } else {
            return
        }
        currTurnIndex = turnIndex
    }

    fun resetGame(){
        for(i in 1..hexGame.gameHistory.size){
            undoTurn()
        }
    }

    fun openGamesHistory(){
        router.navigateTo(screens.history())
    }

    fun backPressed(): Boolean {
        if (isHistoryEditActive) {
            endHistoryEdit()
        } else {
            router.exit()
        }
        return true
    }
}