package com.example.pc.caseproject

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import butterknife.OnClick

class EmergencyDialogFragment : DialogFragment() {
//    lateinit var dialogActionListener: DialogActionListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_emergency_dialog, container, false)
        view?.let {
            dialog.window?.let {
                it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                it.requestFeature(Window.FEATURE_NO_TITLE)
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        view?.findViewById<Button>(R.id.button)?.setOnClickListener { dismiss() }
        view?.findViewById<Button>(R.id.button2)?.setOnClickListener {
            val intent = Intent(context, SOSActivity::class.java)
            startActivity(intent)
        }
    }

//    override fun onDismiss(dialog: DialogInterface?) {
//        super.onDismiss(dialog)
//        dialogActionListener.onDialogDismiss()
//    }

//    interface DialogActionListener {
//        fun onDialogDismiss()
//    }

//    @OnClick(R.id.button)
//    fun onDenyButtonClicked(v: View) {
//        dismiss()
//    }
//
//    @OnClick(R.id.button2)
//    fun onAcceptButtonClicked(v: View) {
//        val intent = Intent(context, SOSActivity::class.java)
//        startActivity(intent)
//    }
}