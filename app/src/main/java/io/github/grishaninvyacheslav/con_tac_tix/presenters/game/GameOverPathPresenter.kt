package io.github.grishaninvyacheslav.con_tac_tix.presenters.game

import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

class GameOverPathPresenter(private val hexField: HexField) {
    private lateinit var winRoot: Pair<CellState, Pair<Int, Int>?>
    lateinit var lastWinPath: List<Pair<Int, Int>>
    private val possibleWinPaths: Queue<List<Pair<Int, Int>>> = LinkedList()
    private val gameHistory = hexField.gameHistory

    private fun extractWinPath(path: List<Pair<Int, Int>>? = null): List<Pair<Int, Int>> {
        val currPath = if (path != null) {
            ArrayList(path)
        } else {
            hexField.fieldState[winRoot.second!!] = Pair(CellState.FREE, null)
            arrayListOf(winRoot.second!!)
        }
        val winRootState = winRoot.first
        while (true) {
            val currPosition = currPath.last()
            var canBeForked = false
            if (
                winRootState == CellState.BOTTOM_ROOTED_FIRST_PLAYER && currPosition.first == hexField.gridSize - 1 ||
                winRootState == CellState.TOP_ROOTED_FIRST_PLAYER && currPosition.first == 0 ||
                winRootState == CellState.LEFT_ROOTED_SECOND_PLAYER && currPosition.second == hexField.gridSize - 1 ||
                winRootState == CellState.RIGHT_ROOTED_SECOND_PLAYER && currPosition.second == 0
            ) {
                return currPath
            }
            fun tryToContinue(position: Pair<Int, Int>) {
                if (hexField.fieldState[position]?.first == winRootState) {
                    if (canBeForked) {
                        possibleWinPaths.add((currPath.clone() as ArrayList<Pair<Int, Int>>).apply {
                            removeLast()
                            add(
                                position
                            )
                        })
                    } else {
                        currPath.add(position)
                    }
                    hexField.fieldState[position] = Pair(CellState.FREE, null)
                    canBeForked = true
                }
            }
            Log.d("[MYLOG]", "winRootState: $winRootState")
            with(Pair(currPosition.first + 1, currPosition.second)) {
                if (hexField.fieldState[this]?.first == winRootState) {
                    currPath.add(this)
                    hexField.fieldState[this] = Pair(CellState.FREE, null)
                    canBeForked = true
                }
            }
            tryToContinue(Pair(currPosition.first - 1, currPosition.second))
            tryToContinue(Pair(currPosition.first, currPosition.second + 1))
            tryToContinue(Pair(currPosition.first, currPosition.second - 1))
            tryToContinue(Pair(currPosition.first + 1, currPosition.second - 1))
            tryToContinue(Pair(currPosition.first - 1, currPosition.second + 1))
            Log.d(
                "[MYLOG]",
                "currPosition: $currPosition, currPath: $currPath, possibleWinPaths: $possibleWinPaths"
            )
            var fieldStateOutput = StringBuilder()
            var j = hexField.gridSize - 1
            while (j >= 0) {
                for (i in 0 until hexField.gridSize) {
                    fieldStateOutput.append("${hexField.fieldState[Pair(j, i)]?.first!!.ordinal} ")
                }
                fieldStateOutput.append("\n")
                j--
            }
            Log.d("[MYLOG]", "curr fieldStateOutput:\n$fieldStateOutput")
            if (!canBeForked) {
                return extractWinPath(possibleWinPaths.remove()) // TODO: с помощью очереди переделать на итеративную реализацию
            }
        }
    }

    fun getWinPath(): List<Pair<Int, Int>> {
        var fieldStateOutput = StringBuilder()
        var j = hexField.gridSize - 1
        while (j >= 0) {
            for (i in 0 until hexField.gridSize) {
                fieldStateOutput.append("${hexField.fieldState[Pair(j, i)]?.first!!.ordinal} ")
            }
            fieldStateOutput.append("\n")
            j--
        }
        Log.d("[MYLOG]", "fieldStateOutput:\n$fieldStateOutput")

        val oppositeState = when (hexField.fieldState[gameHistory.peek().second]!!.first) {
            CellState.BOTTOM_ROOTED_FIRST_PLAYER -> CellState.TOP_ROOTED_FIRST_PLAYER
            CellState.TOP_ROOTED_FIRST_PLAYER -> CellState.BOTTOM_ROOTED_FIRST_PLAYER
            CellState.LEFT_ROOTED_SECOND_PLAYER -> CellState.RIGHT_ROOTED_SECOND_PLAYER
            else -> CellState.LEFT_ROOTED_SECOND_PLAYER
        }
        for (position in hexField.fieldState.keys) {
            if (hexField.fieldState[position]!!.first == oppositeState) {
                hexField.fieldState[position] =
                    Pair(
                        hexField.fieldState[gameHistory.peek().second]!!.first,
                        hexField.fieldState[gameHistory.peek().second]!!.second
                    )
            }
        }
        fieldStateOutput = StringBuilder()
        j = hexField.gridSize - 1
        while (j >= 0) {
            for (i in 0 until hexField.gridSize) {
                fieldStateOutput.append("${hexField.fieldState[Pair(j, i)]?.first!!.ordinal} ")
            }
            fieldStateOutput.append("\n")
            j--
        }
        Log.d("[MYLOG]", "after fieldStateOutput:\n$fieldStateOutput")
        winRoot = hexField.fieldState[hexField.fieldState[gameHistory.peek().second]!!.second]!!
        possibleWinPaths.clear()
        lastWinPath = extractWinPath()
        Log.d("[FIELD_STATE]", "extracted winPath: $lastWinPath")
        return lastWinPath
    }
}