package fr.afaucogney.mobile.android.accidentcounter.feature.counter

import android.arch.lifecycle.MutableLiveData
import fr.afaucogney.mobile.android.accidentcounter.common.archi.base.BaseActivityContract
import org.joda.time.DateTime

class AccidentCounterContract {

    interface ViewCapabilities : BaseActivityContract.ViewCapabilities {

        fun showCurrentDayCountWithoutAccident(dayCount: String)
        fun showDaysRecord(dayCount: String)
        fun showLatestAccidentDate(accident: String)

        fun showAccidentListDialog(accidents: List<Long>)
        fun showNewAccidentDateDialog()
        fun switchToKioskMode()
        fun releaseKioskMode()
        fun goToLockPatternRecord()
        fun goToUnlockPattern()
        fun closeApp()
    }

    interface ViewModel : BaseActivityContract.ViewModel {

        // OUTPUT
        fun observeAccidents(): MutableLiveData<List<Long>>

        fun observeDaysRecord(): MutableLiveData<String>
        fun observeLatestAccident(): MutableLiveData<String>
        fun observeDaysSinceLatestAccident(): MutableLiveData<String>

        fun addNewAccident(accident: DateTime)
        fun removeAccident(accident: Long)
        fun clearAccidents()
        fun updateLockPattern()
    }

}