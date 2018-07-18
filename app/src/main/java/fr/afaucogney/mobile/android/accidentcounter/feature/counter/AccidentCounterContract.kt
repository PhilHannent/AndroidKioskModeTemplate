package fr.afaucogney.mobile.android.accidentcounter.feature.counter

import android.arch.lifecycle.LiveData
import fr.afaucogney.mobile.android.accidentcounter.common.archi.base.BaseActivityContract
import fr.afaucogney.mobile.android.accidentcounter.data.LockPatternDialogEvent
import org.joda.time.DateTime

class AccidentCounterContract {

    interface ViewCapabilities : BaseActivityContract.ViewCapabilities {

        fun showCurrentDayCountWithoutAccident(dayCount: String)
        fun showDaysRecord(dayCount: String)
        fun showLatestAccidentDate(accident: String)

        fun showAccidentListDialog(accidents: List<Long>)
        fun showNewAccidentDateDialog()

        fun goToLockPatternRecordDialog()
        fun goToUnlockPatternDialog()

        fun goToKioskMode()
        fun goToNotKioskMode()

        fun closeApp()
    }

    interface ViewModel : BaseActivityContract.ViewModel {

        // OUTPUT
        fun observeAccidents(): LiveData<List<Long>>

        fun observeDaysRecord(): LiveData<String>
        fun observeLatestAccident(): LiveData<String>
        fun observeDaysSinceLatestAccident(): LiveData<String>

        fun addNewAccident(accident: DateTime)
        fun removeAccident(accident: Long)
        fun clearAccidents()

        fun updateLockPattern(pattern: String)
        fun testLockPattern(pattern: String)
        fun observeLockPatternDialog(): LiveData<LockPatternDialogEvent>
        fun clearLockPattern()

        fun tryToGoToKioskMode()
        fun observeKioskMode(): LiveData<Boolean>
    }

}