package fr.afaucogney.mobile.android.accidentcounter.common.archi.base

import android.content.Context
import android.content.DialogInterface
import android.support.annotation.LayoutRes
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.view.View
import android.widget.FrameLayout

abstract class BaseBottomSheetDialog : BottomSheetDialog {

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    constructor(context: Context) : super(context)

    constructor(context: Context, theme: Int) : super(context, theme)

    protected constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener) : super(context, cancelable, cancelListener)

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // HELPER
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    protected fun setupContentView(@LayoutRes layoutResId: Int) {
        val contentView = layoutInflater.inflate(layoutResId, null)
        setContentView(contentView)
        setupBottomSheetBehavior(contentView)
    }

    protected fun setupBottomSheetBehavior(contentView: View) {
        val mBottomSheetBehavior = BottomSheetBehavior.from(contentView.parent as View)

        mBottomSheetBehavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //showing the different states
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> dismiss() //if you want the modal to be dismissed when user drags the bottomsheet down
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    ///////////////////////////////////////////////////////////////////////////
    // OVERRIDE
    ///////////////////////////////////////////////////////////////////////////

    override fun show() {
        super.show()
        val bottomSheet = findViewById<FrameLayout>(android.support.design.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}
