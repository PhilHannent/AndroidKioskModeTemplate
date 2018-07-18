package fr.afaucogney.mobile.android.accidentcounter.feature.lock

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import fr.afaucogney.mobile.android.accidentcounter.R
import fr.afaucogney.mobile.android.accidentcounter.common.archi.base.BaseDialogFragment
import fr.afaucogney.mobile.android.accidentcounter.data.LockPatternDialogEvent
import fr.afaucogney.mobile.android.accidentcounter.feature.counter.AccidentCounterViewModel
import kotlinx.android.synthetic.main.dialog_fragment_lockview.*
import javax.inject.Inject


class CheckLockViewPatternFragment : BaseDialogFragment() {

    companion object {
        fun newInstance(): CheckLockViewPatternFragment = CheckLockViewPatternFragment()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // DEPENDENCIES
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    lateinit var viewModel: AccidentCounterViewModel


    override val layoutResourceId = R.layout.dialog_fragment_lockview


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        injectDependencies()

        val mPatternLockViewListener = object : PatternLockViewListener {
            override fun onStarted() {
                Log.d(javaClass.name, "Pattern drawing started")
            }

            override fun onProgress(progressPattern: List<PatternLockView.Dot>) {
                Log.d(javaClass.name, "Pattern progress: " + PatternLockUtils.patternToString(plv_kioskLock, progressPattern))
            }

            override fun onComplete(pattern: List<PatternLockView.Dot>) {
                Log.d(javaClass.name, "Pattern complete: " + PatternLockUtils.patternToString(plv_kioskLock, pattern))
                viewModel.testLockPattern(PatternLockUtils.patternToString(plv_kioskLock, pattern))
                viewModel.observeLockPatternDialog().observe(this@CheckLockViewPatternFragment, Observer {
                    when (it) {
                        LockPatternDialogEvent.LOCK -> {
                            plv_kioskLock.setViewMode(PatternLockView.PatternViewMode.WRONG)
                        }
                        LockPatternDialogEvent.UNLOCK -> {
                            plv_kioskLock.setViewMode(PatternLockView.PatternViewMode.CORRECT)
                        }
                    }
                    Handler().postDelayed({ dismiss() }, 1000)
                })
            }

            override fun onCleared() {
                Log.d(javaClass.name, "Pattern has been cleared")
            }
        }
        plv_kioskLock.addPatternLockListener(mPatternLockViewListener)

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRESENTERS EVENTS
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun initViewModelObservations() {

    }


}
