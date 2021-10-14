package io.github.grishaninvyacheslav.con_tac_tix.presenters.game

import android.util.Log

class GameFlowPresenter(private val hexField: HexField) {
    private var currTurnOwner: Owner = Owner.FIRST_PLAYER
    private val gameHistory = hexField.gameHistory
    private val gridSize = hexField.gridSize

    fun makeTurn(position: Pair<Int, Int>){
        if (currTurnOwner == Owner.NO_ONE || hexField.fieldState[position]!!.first != CellState.FREE) {
            return
        }
        hexField.showTurn(position, currTurnOwner)
        currTurnOwner = setCellState(position, currTurnOwner)
        gameHistory.push(
            Pair(
                hexField.fieldState.clone() as HashMap<Pair<Int, Int>, Pair<CellState, Pair<Int, Int>?>>,
                position
            )
        )
        var fieldStateOutput = StringBuilder()
        var j = gridSize - 1
        while (j >= 0) {
            for (i in 0 until gridSize) {
                fieldStateOutput.append("${hexField.fieldState[Pair(j, i)]?.first!!.ordinal} ")
            }
            fieldStateOutput.append("\n")
            j--
        }
        Log.d("[FIELD_STATE]", "afterTurn fieldStateOutput:\n$fieldStateOutput")
        if (currTurnOwner == Owner.NO_ONE) {
            hexField.showGameOver()
            fieldStateOutput = StringBuilder()
            j = gridSize - 1
            while (j >= 0) {
                for (i in 0 until gridSize) {
                    fieldStateOutput.append("${hexField.fieldState[Pair(j, i)]?.first!!.ordinal} ")
                }
                fieldStateOutput.append("\n")
                j--
            }
            Log.d("[FIELD_STATE]", "afterExtraction fieldStateOutput:\n$fieldStateOutput")
        }
    }

    fun undoTurn(){
        if (gameHistory.isEmpty()) {
            return
        }
        with(gameHistory.pop()) {
            if (currTurnOwner == Owner.NO_ONE) {
                hexField.cancelGameOver()
            }
            currTurnOwner = when (this.first[this.second]!!.first) {
                CellState.TOP_ROOTED_FIRST_PLAYER,
                CellState.BOTTOM_ROOTED_FIRST_PLAYER,
                CellState.NOT_ROOTED_FIRST_PLAYER -> Owner.FIRST_PLAYER
                else -> Owner.SECOND_PLAYER
            }
            if (gameHistory.isNotEmpty()) {
                // TODO: баг Kotlin: gameHistory.peek() может вернуть null, но Kotlin этого не знает
                hexField.fieldState =
                    gameHistory.peek().first.clone() as HashMap<Pair<Int, Int>, Pair<CellState, Pair<Int, Int>?>>
            } else {
                hexField.fieldState =
                    hashMapOf<Pair<Int, Int>, Pair<CellState, Pair<Int, Int>?>>().apply {
                        for (i in 0 until gridSize) {
                            for (j in 0 until gridSize) {
                                this[Pair(j, i)] = Pair(CellState.FREE, null)
                            }
                        }
                    }
            }
            hexField.cancelTurn(this.second)
        }
        var fieldStateOutput = StringBuilder()
        var j = gridSize - 1
        while (j >= 0) {
            for (i in 0 until gridSize) {
                fieldStateOutput.append("${hexField.fieldState[Pair(j, i)]?.first!!.ordinal} ")
            }
            fieldStateOutput.append("\n")
            j--
        }
        Log.d("[FIELD_STATE]", "after pop fieldStateOutput:\n$fieldStateOutput")
    }

    private fun spreadBranch(
        position: Pair<Int, Int>,
        rootedBranchState: CellState,
        rootPosition: Pair<Int, Int>
    ): Owner {
        val notRootedBranchState = if (
            rootedBranchState == CellState.BOTTOM_ROOTED_FIRST_PLAYER ||
            rootedBranchState == CellState.TOP_ROOTED_FIRST_PLAYER
        ) {
            CellState.NOT_ROOTED_FIRST_PLAYER
        } else {
            CellState.NOT_ROOTED_SECOND_PLAYER
        }
        val oppositeBranchState = when (rootedBranchState) {
            CellState.BOTTOM_ROOTED_FIRST_PLAYER -> CellState.TOP_ROOTED_FIRST_PLAYER
            CellState.TOP_ROOTED_FIRST_PLAYER -> CellState.BOTTOM_ROOTED_FIRST_PLAYER
            CellState.LEFT_ROOTED_SECOND_PLAYER -> CellState.RIGHT_ROOTED_SECOND_PLAYER
            else -> CellState.LEFT_ROOTED_SECOND_PLAYER
        }

        fun tryToSpread(position: Pair<Int, Int>): Owner {
            if (hexField.fieldState[position]?.first == notRootedBranchState) {
                hexField.fieldState[position] = Pair(rootedBranchState, rootPosition)
                spreadBranch(
                    position,
                    rootedBranchState,
                    rootPosition
                ) // TODO: с помощью очереди переделать на итеративную реализацию
            }
            return if (currTurnOwner == Owner.FIRST_PLAYER) Owner.SECOND_PLAYER else Owner.FIRST_PLAYER
        }
        tryToSpread(
            Pair(
                position.first + 1,
                position.second
            )
        ).also { if (it == Owner.NO_ONE) return@spreadBranch it }
        tryToSpread(
            Pair(
                position.first - 1,
                position.second
            )
        ).also { if (it == Owner.NO_ONE) return@spreadBranch it }
        tryToSpread(
            Pair(
                position.first,
                position.second + 1
            )
        ).also { if (it == Owner.NO_ONE) return@spreadBranch it }
        tryToSpread(
            Pair(
                position.first,
                position.second - 1
            )
        ).also { if (it == Owner.NO_ONE) return@spreadBranch it }
        tryToSpread(
            Pair(
                position.first + 1,
                position.second - 1
            )
        ).also { if (it == Owner.NO_ONE) return@spreadBranch it }
        tryToSpread(
            Pair(
                position.first - 1,
                position.second + 1
            )
        ).also { if (it == Owner.NO_ONE) return@spreadBranch it }
        if (hexField.fieldState[position]!!.first == rootedBranchState) {
            when (oppositeBranchState) {
                hexField.fieldState[Pair(position.first + 1, position.second)]?.first,
                hexField.fieldState[Pair(position.first - 1, position.second)]?.first,
                hexField.fieldState[Pair(position.first, position.second + 1)]?.first,
                hexField.fieldState[Pair(position.first, position.second - 1)]?.first,
                hexField.fieldState[Pair(position.first + 1, position.second - 1)]?.first,
                hexField.fieldState[Pair(position.first - 1, position.second + 1)]?.first
                ->
                    return Owner.NO_ONE
            }
        }
        return if (currTurnOwner == Owner.FIRST_PLAYER) Owner.SECOND_PLAYER else Owner.FIRST_PLAYER
    }

    private fun tryToRoot(position: Pair<Int, Int>, cellState: CellState): Owner {
        when (cellState) {
            hexField.fieldState[Pair(position.first + 1, position.second)]?.first,
            hexField.fieldState[Pair(position.first - 1, position.second)]?.first,
            hexField.fieldState[Pair(position.first, position.second + 1)]?.first,
            hexField.fieldState[Pair(position.first, position.second - 1)]?.first,
            hexField.fieldState[Pair(position.first + 1, position.second - 1)]?.first,
            hexField.fieldState[Pair(position.first - 1, position.second + 1)]?.first
            -> {
                if (cellState == hexField.fieldState[Pair(position.first + 1, position.second)]?.first) {
                    hexField.fieldState[position] = Pair(
                        cellState,
                        hexField.fieldState[Pair(position.first + 1, position.second)]!!.second
                    )
                } else if (cellState == hexField.fieldState[Pair(
                        position.first - 1,
                        position.second
                    )]?.first
                ) {
                    hexField.fieldState[position] = Pair(
                        cellState,
                        hexField.fieldState[Pair(position.first - 1, position.second)]!!.second
                    )
                } else if (cellState == hexField.fieldState[Pair(
                        position.first,
                        position.second + 1
                    )]?.first
                ) {
                    hexField.fieldState[position] = Pair(
                        cellState,
                        hexField.fieldState[Pair(position.first, position.second + 1)]!!.second
                    )
                } else if (cellState == hexField.fieldState[Pair(
                        position.first,
                        position.second - 1
                    )]?.first
                ) {
                    hexField.fieldState[position] = Pair(
                        cellState,
                        hexField.fieldState[Pair(position.first, position.second - 1)]!!.second
                    )
                } else if (cellState == hexField.fieldState[Pair(
                        position.first + 1,
                        position.second - 1
                    )]?.first
                ) {
                    hexField.fieldState[position] = Pair(
                        cellState,
                        hexField.fieldState[Pair(position.first + 1, position.second - 1)]!!.second
                    )
                } else if (cellState == hexField.fieldState[Pair(
                        position.first - 1,
                        position.second + 1
                    )]?.first
                ) {
                    hexField.fieldState[position] = Pair(
                        cellState,
                        hexField.fieldState[Pair(position.first - 1, position.second + 1)]!!.second
                    )
                }
                if (currTurnOwner == Owner.FIRST_PLAYER) {
                    if ((position.first == gridSize - 1) && cellState == CellState.BOTTOM_ROOTED_FIRST_PLAYER) {
                        return Owner.NO_ONE
                    } else if ((position.first == 0) && cellState == CellState.TOP_ROOTED_FIRST_PLAYER) {
                        return Owner.NO_ONE
                    }
                } else if (currTurnOwner == Owner.SECOND_PLAYER) {
                    if ((position.second == gridSize - 1) && cellState == CellState.LEFT_ROOTED_SECOND_PLAYER) {
                        return Owner.NO_ONE
                    } else if ((position.second == 0) && cellState == CellState.RIGHT_ROOTED_SECOND_PLAYER) {
                        return Owner.NO_ONE
                    }
                }
                return spreadBranch(position, cellState, hexField.fieldState[position]!!.second!!)
            }
            else -> return if (currTurnOwner == Owner.FIRST_PLAYER) Owner.SECOND_PLAYER else Owner.FIRST_PLAYER
        }
    }

    private fun setCellState(position: Pair<Int, Int>, currTurnOwner: Owner): Owner {
        if (currTurnOwner == Owner.FIRST_PLAYER && position.first == 0) {
            CellState.BOTTOM_ROOTED_FIRST_PLAYER.let {
                hexField.fieldState[position] = Pair(it, position)
                return spreadBranch(position, it, position)
            }
        } else if (currTurnOwner == Owner.FIRST_PLAYER && position.first == gridSize - 1) {
            CellState.TOP_ROOTED_FIRST_PLAYER.also {
                hexField.fieldState[position] = Pair(it, position)
                return spreadBranch(position, it, position)
            }
        } else if (currTurnOwner == Owner.SECOND_PLAYER && position.second == 0) {
            CellState.LEFT_ROOTED_SECOND_PLAYER.also {
                hexField.fieldState[position] = Pair(it, position)
                return spreadBranch(position, it, position)
            }
        } else if (currTurnOwner == Owner.SECOND_PLAYER && position.second == gridSize - 1) {
            CellState.RIGHT_ROOTED_SECOND_PLAYER.also {
                hexField.fieldState[position] = Pair(it, position)
                return spreadBranch(position, it, position)
            }
        }
        if (currTurnOwner == Owner.FIRST_PLAYER) {
            hexField.fieldState[position] = Pair(CellState.NOT_ROOTED_FIRST_PLAYER, null)
        } else {
            hexField.fieldState[position] = Pair(CellState.NOT_ROOTED_SECOND_PLAYER, null)
        }
        return if (currTurnOwner == Owner.FIRST_PLAYER) {
            var nextTurnOwner = tryToRoot(position, CellState.BOTTOM_ROOTED_FIRST_PLAYER)
            if (hexField.fieldState[position]!!.first == CellState.BOTTOM_ROOTED_FIRST_PLAYER) {
                nextTurnOwner
            } else {
                tryToRoot(position, CellState.TOP_ROOTED_FIRST_PLAYER)
            }
        } else {
            var nextTurnOwner = tryToRoot(position, CellState.LEFT_ROOTED_SECOND_PLAYER)
            if (hexField.fieldState[position]!!.first == CellState.LEFT_ROOTED_SECOND_PLAYER) {
                nextTurnOwner
            } else {
                tryToRoot(position, CellState.RIGHT_ROOTED_SECOND_PLAYER)
            }
        }
    }
}