package io.github.grishaninvyacheslav.con_tac_tix.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import io.github.grishaninvyacheslav.con_tac_tix.databinding.FragmentHistoryBinding
import io.github.grishaninvyacheslav.con_tac_tix.presenters.history.HistoryPresenter
import io.github.grishaninvyacheslav.con_tac_tix.presenters.history.HistoryView
import io.github.grishaninvyacheslav.con_tac_tix.ui.BackButtonListener
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class GamesHistoryFragment : MvpAppCompatFragment(), HistoryView, BackButtonListener {
    private val view: FragmentHistoryBinding by viewBinding(createMethod = CreateMethod.INFLATE)

    private val presenter: HistoryPresenter by moxyPresenter {
        HistoryPresenter()
    }

    companion object {
        fun newInstance() = GamesHistoryFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = view.root

    override fun backPressed() = presenter.backPressed()
}