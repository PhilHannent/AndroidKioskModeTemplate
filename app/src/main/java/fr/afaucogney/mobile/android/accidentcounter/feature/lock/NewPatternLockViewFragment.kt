package fr.afaucogney.mobile.android.accidentcounter.feature.lock

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import fr.afaucogney.mobile.android.accidentcounter.R
import fr.afaucogney.mobile.android.accidentcounter.common.archi.base.BaseDialogFragment
import fr.afaucogney.mobile.android.accidentcounter.feature.counter.AccidentCounterViewModel
import kotlinx.android.synthetic.main.dialog_fragment_lockview.*
import javax.inject.Inject


class NewPatternLockViewFragment : BaseDialogFragment() {

    companion object {
        fun newInstance(): NewPatternLockViewFragment = NewPatternLockViewFragment()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // DEPENDENCIES
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    lateinit var mViewModel: AccidentCounterViewModel


    override val layoutResourceId = R.layout.dialog_fragment_lockview

    var firstSymbol: String? = null
    var secondSymbol: String? = null

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        injectDependencies()

        tv_lockTitle.text = "1/ Entrez votre nouveau symbol"

        val mPatternLockViewListener = object : PatternLockViewListener {
            override fun onStarted() {
                Log.d(javaClass.name, "Pattern drawing started")
            }

            override fun onProgress(progressPattern: List<PatternLockView.Dot>) {
                Log.d(javaClass.name, "Pattern progress: " + PatternLockUtils.patternToString(plv_kioskLock, progressPattern))
            }

            override fun onComplete(pattern: List<PatternLockView.Dot>) {
                PatternLockUtils.patternToString(plv_kioskLock, pattern).let {
                    Log.d(javaClass.name, "Pattern complete: $it")
                    if (firstSymbol == null) {
                        firstSymbol = it
                        plv_kioskLock.clearPattern()
                        tv_lockTitle.text = "2/ Confirmez votre nouveau symbol"
                    } else {
                        secondSymbol = it
                        if (firstSymbol == secondSymbol) {
                            plv_kioskLock.setViewMode(PatternLockView.PatternViewMode.CORRECT)
                            mViewModel.updateLockPattern(PatternLockUtils.patternToString(plv_kioskLock, pattern))
                            Handler().postDelayed({ dismiss() }, 1000)
                        } else {
                            firstSymbol = null
                            secondSymbol = null
                            plv_kioskLock.setViewMode(PatternLockView.PatternViewMode.WRONG)
                            Handler().postDelayed({
                                plv_kioskLock.clearPattern()
                                tv_lockTitle.text = "1/ Erreur - Ré-entrez votre nouveau symbol"
                            }, 1000)
                        }
                    }
                }
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
