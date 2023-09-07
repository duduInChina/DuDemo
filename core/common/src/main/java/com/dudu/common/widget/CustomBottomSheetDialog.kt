package com.dudu.common.widget

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.dudu.common.R
import com.dudu.common.util.DensityUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * 底部弹出的Dialog
 * Created by Dzc on 2023/8/31.
 */
class CustomBottomSheetDialog(val content: Fragment) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflate = inflater.inflate(R.layout.dialog_bottom_sheet, container)
        childFragmentManager.beginTransaction().replace(R.id.dialog_bottom_sheet_content, content).commit()
        return inflate
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (dialog != null && dialog is BottomSheetDialog) {
            val sheetDialog = dialog as BottomSheetDialog
            sheetDialog.window?.setDimAmount(0f)
            val behavior = sheetDialog.behavior
            behavior.skipCollapsed = true // 下拉时是否可以跳过折叠状态直接隐藏
//            behavior.expandedOffset = 300
            behavior.maxHeight = (DensityUtils.getScreenHeightPixels() * 0.6).toInt()
            sheetDialog.setOnShowListener {
                behavior.state = STATE_EXPANDED
            }
        }
    }

}