package io.github.grishaninvyacheslav.con_tac_tix.presenters.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.grishaninvyacheslav.con_tac_tix.databinding.ItemHistoryListBinding
import io.github.grishaninvyacheslav.con_tac_tix.presenters.history.HistoryItemView
import io.github.grishaninvyacheslav.con_tac_tix.presenters.history.IHistoryListPresenter

class GameHistoryRVAdapter(
    private val presenter: IHistoryListPresenter,
) : RecyclerView.Adapter<GameHistoryRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemHistoryListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).apply {
            itemView.setOnClickListener { presenter.itemClickListener?.invoke(this) }
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        presenter.bindView(holder.apply { pos = position })

    override fun getItemCount() = presenter.getCount()

    inner class ViewHolder(private val view: ItemHistoryListBinding) :
        RecyclerView.ViewHolder(view.root),
        HistoryItemView {
        override var pos = -1

        override fun setTurnPosition(turnPosition: String) {
            view.turnPosition.text = turnPosition
        }

        override fun setColor(color: Int) {
            view.turnPosition.setTextColor(color)
        }
    }
}