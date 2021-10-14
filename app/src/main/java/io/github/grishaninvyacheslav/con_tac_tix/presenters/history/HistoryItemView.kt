package io.github.grishaninvyacheslav.con_tac_tix.presenters.history

import io.github.grishaninvyacheslav.con_tac_tix.presenters.IItemView

interface HistoryItemView: IItemView {
    fun setTurnPosition(turnPosition: String)
    fun setColor(color: Int)
}