package com.antonin.friendswave.outils

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.antonin.friendswave.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.res.ResourcesCompat.getDrawable

class AlertDialog(private var context: Context): DialogInterface {

    fun onCreateDialog(str:String) {

        val alertDialogBuilder = AlertDialog.Builder(context)
        with(alertDialogBuilder) {
            setTitle("Avertissement")
            setMessage(str)
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton("CANCEL", null)
        }

        alertDialogBuilder.create()

        alertDialogBuilder.show()


    }

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(context,
            android.R.string.yes, Toast.LENGTH_SHORT).show()
    }



    fun showDialog(context: Context, title: String, msg: String,
                   positiveBtnText: String, negativeBtnText: String?,
                   positiveBtnClickListener: DialogInterface.OnClickListener,
                   negativeBtnClickListener: DialogInterface.OnClickListener?): AlertDialog {
        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(msg)
            .setCancelable(true)
            .setPositiveButton(positiveBtnText, positiveBtnClickListener)
        if (negativeBtnText != null)
            builder.setNegativeButton(negativeBtnText, negativeBtnClickListener)
        val alert = builder.create()
        alert.show()
        return alert
    }


    override fun cancel() {
        TODO("Not yet implemented")
    }

    override fun dismiss() {
        TODO("Not yet implemented")
    }

}