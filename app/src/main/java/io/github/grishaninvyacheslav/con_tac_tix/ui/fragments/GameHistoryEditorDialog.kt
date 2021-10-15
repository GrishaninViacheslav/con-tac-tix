package io.github.grishaninvyacheslav.con_tac_tix.ui.fragments

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import io.github.grishaninvyacheslav.con_tac_tix.R
import io.github.grishaninvyacheslav.con_tac_tix.databinding.DialogEditGameHistoryBinding

class GameHistoryEditorDialog : DialogFragment() {
    private var _view: DialogEditGameHistoryBinding? = null
    private val view get() = _view!!

    private lateinit var listener: GameHistoryEditorDialogListener

    interface GameHistoryEditorDialogListener {
        fun onDialogPositiveClick(gameHistory: String)
    }

    companion object {
        private const val GAME_HISTORY_KEY = "GAME_HISTORY"

        fun newInstance(gameHistory: String, listener: GameHistoryEditorDialogListener) =
            GameHistoryEditorDialog().apply {
                arguments = Bundle().apply {
                    putString(GAME_HISTORY_KEY, gameHistory)
                }
                this.listener = listener
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            _view = DialogEditGameHistoryBinding.inflate(requireActivity().layoutInflater)
            return AlertDialog.Builder(it)
                .setView(view.root.apply {
                    view.gameHistory.text =
                        SpannableStringBuilder(requireArguments().getString(GAME_HISTORY_KEY))
                })
                .setTitle(R.string.game_history_editor)
                .setPositiveButton(
                    R.string.change_game_history
                ) { _, _ -> listener.onDialogPositiveClick(view.gameHistory.text.toString()) }
                .setNeutralButtonIcon(
                    ResourcesCompat.getDrawable(resources, R.drawable.outline_copy_all_24, null)
                )
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ -> return@setNegativeButton }
                .create().apply {
                    setOnShowListener {
                        with((this).getButton(AlertDialog.BUTTON_NEUTRAL)) {
                            setOnClickListener {
                                val clip = ClipData.newPlainText(
                                    getString(R.string.game_history_editor),
                                    view.gameHistory.text
                                )
                                requireContext().getSystemService(ClipboardManager::class.java)
                                    .setPrimaryClip(clip)
                                Toast.makeText(context, R.string.text_copied, Toast.LENGTH_SHORT)
                                    .show()
                                return@setOnClickListener
                            }
                        }
                    }
                }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _view = null
    }
}