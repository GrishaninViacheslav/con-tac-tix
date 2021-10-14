package io.github.grishaninvyacheslav.con_tac_tix.ui.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import io.github.grishaninvyacheslav.con_tac_tix.R

class GameHistoryEditorFragment : DialogFragment() {
    companion object {
        private const val GAME_HISTORY_KEY = "GAME_HISTORY"

        fun newInstance(gameHistory: String) =
            GameHistoryEditorFragment().apply {
                arguments = Bundle().apply {
                    putString(GAME_HISTORY_KEY, gameHistory)
                }
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
                .setView(inflater.inflate(R.layout.dialog_edit_game_history, null).apply {
                    findViewById<EditText>(R.id.game_history).text =
                        SpannableStringBuilder(requireArguments().getString(GAME_HISTORY_KEY))
                })
                .setPositiveButton(R.string.change_game_history,
                    DialogInterface.OnClickListener { dialog, id ->
                        // FIRE ZE MISSILES!
                    })
                .setNeutralButton(
                    "",
                    DialogInterface.OnClickListener { dialog, id ->
                        // TODO: скопировать в буффер обмена и вывести toast об этом
                    }
                )
                .setNeutralButtonIcon(
                    ResourcesCompat.getDrawable(resources, R.drawable.outline_copy_all_24, null)
                )
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}