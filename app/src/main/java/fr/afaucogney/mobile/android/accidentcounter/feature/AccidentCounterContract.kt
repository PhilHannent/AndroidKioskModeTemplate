package fr.afaucogney.mobile.android.accidentcounter.feature

import android.arch.lifecycle.MutableLiveData
import fr.afaucogney.mobile.android.accidentcounter.common.archi.base.BaseActivityContract
import org.joda.time.DateTime

class AccidentCounterContract {

    interface ViewCapabilities : BaseActivityContract.ViewCapabilities {

        fun showCurrentDayCountWithoutAccident(dayCount: String)
        fun showDaysRecord(dayCount: String)
        fun showLatestAccidentDate(accident: String)

        fun showAccidentListDialog(accidents: List<String>)
        fun showNewAccidentDateDialog()
        fun switchToKioskMode()
        fun releaseKioskMode()
        fun goToLockPatternRecord()
        fun goToUnlockPattern()
        fun closeApp()
    }

    interface ViewModel : BaseActivityContract.ViewModel {

        // OUTPUT
        fun observeAccidents(): MutableLiveData<List<String>>

        fun observeDaysRecord(): MutableLiveData<String>
        fun observeLatestAccident(): MutableLiveData<String>
        fun observeDaysSinceLatestAccident(): MutableLiveData<String>

        fun addNewAccident(accident: DateTime)
        fun removeAccident(accident: DateTime)
        fun updateLockPattern()
    }

}