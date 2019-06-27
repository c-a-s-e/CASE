package com.example.pc.caseproject

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_tutorial.*
import kotlinx.android.synthetic.main.item_showcase.view.*


class ShowcaseDialog : DialogFragment() {
    val images = listOf(R.drawable.t1, R.drawable.t2, R.drawable.t3, R.drawable.t4)
    val descriptions = listOf(R.string.description_1, R.string.description_2, R.string.description_3, R.string.description_4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogStyle)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.activity_tutorial, container, false)
    }

    override fun onResume() {
        super.onResume()
        slider.pageCount = images.size
        slider.setViewListener { position ->
            val customView = layoutInflater.inflate(R.layout.item_showcase, null)
            Glide.with(this).load(images[position]).into(customView.image)
            customView.description.text = context?.resources?.getString(descriptions[position])
            customView
        }
        skip.setOnClickListener { dismiss()}
    }
}
