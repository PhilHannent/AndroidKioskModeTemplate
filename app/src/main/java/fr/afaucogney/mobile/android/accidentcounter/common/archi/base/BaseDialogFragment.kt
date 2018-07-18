package fr.afaucogney.mobile.android.accidentcounter.common.archi.base

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import fr.afaucogney.mobile.android.accidentcounter.common.archi.utils.LogUtil
import toothpick.Toothpick

abstract class BaseDialogFragment : DialogFragment() {

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // DATA
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @get:LayoutRes
    protected abstract val layoutResourceId: Int

    lateinit var unbinder: Unbinder

    companion object {
        const val DIALOG = "DIALOG"
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // LIFE CYCLE
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LogUtil.i(this, "onCreateView")
        val view = inflater.inflate(layoutResourceId, container, false)
        unbinder = ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        LogUtil.i(this, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)
        initViewModelObservations()
    }

    @CallSuper
    override fun onDestroyView() {
        LogUtil.i(this, "onDestroyView")
        super.onDestroyView()
        unbinder.unbind()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // HELPERS
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    protected fun injectDependencies() {
        val activityScope = Toothpick.openScopes(activity, this)
        Toothpick.inject(this, activityScope)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    fun show(fragmentManager: FragmentManager) {
        val ft = fragmentManager.beginTransaction()
        val prev = fragmentManager.findFragmentByTag(DIALOG)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(android.R.id.content, this, DIALOG)
                .commit()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // ABSTRACT API
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun initViewModelObservations() {}
}
