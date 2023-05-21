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

class AlertDialog: DialogInterface {



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
        return
    }

    override fun dismiss() {
        return
    }



}