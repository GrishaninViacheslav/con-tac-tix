package io.github.grishaninvyacheslav.con_tac_tix.ui.fragments

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import io.github.grishaninvyacheslav.con_tac_tix.App
import io.github.grishaninvyacheslav.con_tac_tix.R
import io.github.grishaninvyacheslav.con_tac_tix.databinding.FragmentGameBinding
import io.github.grishaninvyacheslav.con_tac_tix.presenters.game.GamePresenter
import io.github.grishaninvyacheslav.con_tac_tix.presenters.game.GameView
import io.github.grishaninvyacheslav.con_tac_tix.presenters.game.Owner
import io.github.grishaninvyacheslav.con_tac_tix.presenters.history.CenterLinearLayoutManager
import io.github.grishaninvyacheslav.con_tac_tix.presenters.game.GameHistoryRVAdapter
import io.github.grishaninvyacheslav.con_tac_tix.ui.BackButtonListener
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.lang.StringBuilder
import kotlin.math.round


class GameFragment : MvpAppCompatFragment(), GameView, BackButtonListener,
    GameHistoryEditorDialog.GameHistoryEditorDialogListener {
    private var _view: FragmentGameBinding? = null
    private val view get() = _view!!

    private val presenter: GamePresenter by moxyPresenter {
        GamePresenter()
    }
    private var adapterGame: GameHistoryRVAdapter? = null

    private val playerCellBorderRadius = 18
    private val emptyCellBorderRadius = 9

    private val xAxisNumbers = App.instance.resources.getStringArray(R.array.x_axis_numbers)

    private val vibrator = object {
        private var isVibrationCanceled = false

        fun vibrate(repeat: Int) {
            Thread {
                isVibrationCanceled = false
                val v = requireContext().getSystemService(Vibrator::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createOneShot(50, 255)
                    for (i in 1..repeat) {
                        Thread.sleep(100)
                        if (isVibrationCanceled) {
                            return@Thread
                        }
                        v!!.vibrate(effect)
                    }

                } else {
                    for (i in 1..repeat) {
                        Thread.sleep(100)
                        if (isVibrationCanceled) {
                            return@Thread
                        }
                        v!!.vibrate(50)
                    }
                }
            }.start()
        }

        fun cancelVibration() {
            isVibrationCanceled = true
        }
    }


    private fun getBorderParams(colorOwner: Owner): Pair<List<Int>, Int> = when (colorOwner) {
        Owner.FIRST_PLAYER -> Pair(
            listOf(
                App.instance.getColor(R.color.blue_2),
                App.instance.getColor(R.color.blue_4),
                App.instance.getColor(R.color.blue_6),
                App.instance.getColor(R.color.blue_5),
                App.instance.getColor(R.color.blue_3),
                App.instance.getColor(R.color.blue_1)
            ), playerCellBorderRadius
        )
        Owner.SECOND_PLAYER -> Pair(
            listOf(
                App.instance.getColor(R.color.red_2),
                App.instance.getColor(R.color.red_4),
                App.instance.getColor(R.color.red_6),
                App.instance.getColor(R.color.red_5),
                App.instance.getColor(R.color.red_3),
                App.instance.getColor(R.color.red_1)
            ), playerCellBorderRadius
        )
        Owner.NO_ONE -> Pair(
            listOf(
                App.instance.getColor(R.color.light_gray),
                App.instance.getColor(R.color.light_gray),
                App.instance.getColor(R.color.light_gray),
                App.instance.getColor(R.color.light_gray),
                App.instance.getColor(R.color.light_gray),
                App.instance.getColor(R.color.light_gray)
            ), emptyCellBorderRadius
        )
    }

    private fun getColor(colorOwner: Owner): Int = when (colorOwner) {
        Owner.FIRST_PLAYER -> App.instance.getColor(R.color.blue_0)
        Owner.SECOND_PLAYER -> App.instance.getColor(R.color.red_0)
        Owner.NO_ONE -> Color.WHITE
    }

    companion object {
        fun newInstance() = GameFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _view = FragmentGameBinding.inflate(inflater, container, false).apply {
            gameBoard.apply {
                onCellClickListener = { _, position ->
                    presenter.makeTurn(position)
                }
                onBackgroundClickListener = {
                    presenter.endHistoryEdit()
                }
            }
            undoTurn.setOnClickListener { presenter.undoTurn() }
            undoTurn.setOnLongClickListener {
                presenter.resetGame()
                return@setOnLongClickListener true
            }
            history.setOnClickListener { presenter.editHistory() }
            hideHistoryButton.setOnClickListener { presenter.endHistoryEdit() }
            openGamesHistory.setOnClickListener { presenter.openGamesHistory() }
        }
        return view.root
    }

    override fun cancelGameOverMessage(positions: List<Pair<Int, Int>>) {
        view.gameBoard.cancelAnimation(positions)
        vibrator.cancelVibration()
    }

    override fun showHistoryEditor() {
        view.menu.visibility = View.GONE
        view.historyEditor.visibility = View.VISIBLE
    }

    override fun updateHistory() {
        adapterGame?.let {
            it.notifyDataSetChanged()
            view.gameHistoryList.scrollToPosition(if (it.itemCount == 0) 0 else it.itemCount - 1);
        }
    }

    override fun closeHistoryEditor() {
        view.menu.visibility = View.VISIBLE
        view.historyEditor.visibility = View.GONE
    }

    override fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun init() {
        with(view) {
            gameHistoryList.layoutManager =
                CenterLinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            gameHistoryList.clipToPadding = false
            adapterGame = GameHistoryRVAdapter(presenter.historyListPresenter)
            gameHistoryList.adapter = adapterGame
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(gameHistoryList)
            gameHistoryList.onFlingListener = snapHelper
            editGameHistory.setOnClickListener {
                val gameHistory = StringBuilder()
                for (turn in presenter.historyListPresenter.currGameHistory) {
                    gameHistory.append(
                        String.format(
                            getString(R.string.position),
                            xAxisNumbers[turn.first],
                            turn.second + 1
                        )
                    )
                    if (turn != presenter.historyListPresenter.currGameHistory.last()) {
                        gameHistory.append(", ")
                    }
                }
                val dialog =
                    GameHistoryEditorDialog.newInstance(gameHistory.toString(), this@GameFragment)
                dialog.show(requireActivity().supportFragmentManager, "GameHistoryEditorFragment")
            }
            gameHistoryList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                private var lastSelectedViewPosition = -1

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!gameHistoryList.isVisible) {
                        return
                    }
                    recyclerView.post {
                        val firstVisibleIndex =
                            (gameHistoryList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                        val lastVisibleIndex =
                            (gameHistoryList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                        val visibleIndexes = listOf(firstVisibleIndex..lastVisibleIndex).flatten()

                        for (i in visibleIndexes) {
                            val vh = gameHistoryList.findViewHolderForLayoutPosition(i)
                            if (vh?.itemView == null) {
                                continue
                            }
                            val location = IntArray(2)
                            vh.itemView.getLocationOnScreen(location)
                            val x = location[0]
                            val rightSide = x + vh.itemView.width
                            val isInMiddle =
                                round(gameHistoryList.width * .5).toInt() in x..rightSide
                            Log.d(
                                "[MYLOG]",
                                "i: $i\ngameHistoryList.width * .5: ${gameHistoryList.width * .5}\nx: $x\nrightSide: $rightSide\nlastSelectedViewPosition: $lastSelectedViewPosition"
                            )
                            if (isInMiddle && i != lastSelectedViewPosition) {
                                lastSelectedViewPosition = i
                                adapterGame?.let { adapter ->
                                    for (j in 0..adapter.itemCount) {
                                        gameHistoryList.findViewHolderForLayoutPosition(j)?.let {
                                            with(it as GameHistoryRVAdapter.ViewHolder) {
                                                if (j <= i) {
                                                    if (j % 2 == 0) {
                                                        this.setColor(App.instance.getColor(R.color.blue_0))
                                                    } else {
                                                        this.setColor(App.instance.getColor(R.color.red_0))
                                                    }
                                                } else {
                                                    this.setColor(App.instance.getColor(R.color.light_gray))
                                                }
                                            }
                                        }
                                    }
                                }
                                presenter.showGameBoardAtTurnIndex(i)
                                return@post
                            }
                        }
                    }
                }
            })
        }
    }

    override fun showTurn(position: Pair<Int, Int>, turnOwner: Owner) {
        view.gameBoard.setBorderParams(position, getColor(turnOwner), getBorderParams(turnOwner))
    }

    override fun cancelTurn(position: Pair<Int, Int>) {
        view.gameBoard.setBorderParams(
            position,
            getColor(Owner.NO_ONE),
            getBorderParams(Owner.NO_ONE)
        )
    }

    override fun showGameOverPath(positions: List<Pair<Int, Int>>) {
        view.gameBoard.startAnimation(positions)
        vibrator.vibrate(positions.size)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _view = null
    }

    override fun onDialogPositiveClick(gameHistory: String) {
        presenter.applyGame(gameHistory)
    }

    override fun backPressed() = presenter.backPressed()
}