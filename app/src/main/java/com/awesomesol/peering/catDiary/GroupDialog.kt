package com.awesomesol.peering.catDiary

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.EditText
import com.awesomesol.peering.R
import kotlinx.android.synthetic.main.catdiary_sharediary_create_dialog.*

class GroupDialog(context: Context) {
    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener

    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }

    fun showDialog()
    {
        dialog.setContentView(R.layout.catdiary_sharediary_create_dialog)
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val createGroup = dialog.findViewById<EditText>(R.id.et_catDiaryFragment_group)

        dialog.btn_catDiaryFragment_group_finish.setOnClickListener {
            onClickListener.onClicked(createGroup.text.toString())
            dialog.dismiss()
        }
    }

    interface OnDialogClickListener
    {
        fun onClicked(name: String)
    }
}