package com.example.pc.caseproject

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import kotlinx.android.synthetic.main.fragment_emergency_dialog.*

class EmergencyDialogFragment : DialogFragment() {
    lateinit var message: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_emergency_dialog, container, false)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestFeature(Window.FEATURE_NO_TITLE)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        dialog_textview.text = message
        button.setOnClickListener { dismiss() }
        button2.setOnClickListener {
            val intent = Intent(context, FindAEDActivity::class.java)
            startActivity(intent)
            dismiss()
        }
    }
}