package fr.afaucogney.mobile.android.accidentcounter.common.archi.base

import android.app.Application
import android.app.Dialog
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import butterknife.ButterKnife
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import fr.afaucogney.mobile.android.accidentcounter.common.archi.utils.LogUtil
import toothpick.Toothpick
import toothpick.config.Module
import toothpick.smoothie.module.SmoothieActivityModule
import toothpick.smoothie.module.SmoothieSupportActivityModule

abstract class BaseActivity : AppCompatActivity(), BaseActivityContract.ViewCapabilities {

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // DEPENDENCIES
    ////////////////////////////////////////////////////////////////////////////////////////////////////

//    @Inject
//    lateinit var orientationStateProvider : OrientationStateProvider

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // DATA
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract val injectionModule: Module

    protected val scopeProvider by lazy { AndroidLifecycleScopeProvider.from(this) }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // VIEWS
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private var currentDialog: Dialog? = null

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // CONFIGURATION
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @get:LayoutRes
    protected abstract val layoutResourceId: Int

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // LIFE CYCLE
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @CallSuper
    override fun onStart() {
        LogUtil.i(this, "onStart")
        super.onStart()
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtil.i(this, "onCreate")
        super.onCreate(savedInstanceState)
        injectDependencies()
//        setupOrientation()
        setContentView(layoutResourceId)
        ButterKnife.bind(this)
        initViewModelObservations()
    }

    @CallSuper
    override fun onResume() {
        LogUtil.i(this, "onResume")
        super.onResume()
    }

    @CallSuper
    override fun onDestroy() {
        LogUtil.i(this, "onDestroy")
        Toothpick.closeScope(this)
        super.onDestroy()
    }

    override fun onStop() {
        LogUtil.i(this, "onStop")
        super.onStop()
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // ABSTRACT API
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract fun initViewModelObservations()

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // SPECIALISATION
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun showProgress() {}

    override fun hideProgress() {}

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // HELPER
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun injectDependencies() {
        val activityScope = Toothpick.openScopes(application, this)
        activityScope.installModules(SmoothieSupportActivityModule(this), SmoothieActivityModule(this), BaseActivityModule(this), injectionModule)
        Toothpick.inject(this, activityScope)
    }

    protected fun showDialog(dialog: Dialog) {
        // Dismiss current dialog (includes bottom sheet) if there is any
        currentDialog?.dismiss()
        // Show new bottom dialog
        dialog.show()
        currentDialog = dialog
    }

//    private fun setupOrientation() {
//        if (orientationStateProvider.isTablet()){
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        }
//    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // EXTENSIONS
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    fun <T : ViewModel> AppCompatActivity.getViewModel(viewModelClass: Class<T>): T =
            ViewModelProviders.of(this, Toothpick.openScopes(application as Application)
                    .getInstance(ViewModelFactory::class.java))
                    .get(viewModelClass)
}
