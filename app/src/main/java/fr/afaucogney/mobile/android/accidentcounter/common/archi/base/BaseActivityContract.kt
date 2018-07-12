package fr.afaucogney.mobile.android.accidentcounter.common.archi.base

import io.reactivex.Observable
import fr.afaucogney.mobile.android.accidentcounter.common.archi.UIMessage

open class BaseActivityContract {

    interface ViewCapabilities {
        fun showProgress()
        fun hideProgress()
        //fun showSnackbar(message: UIMessage)
    }

    interface ViewModel {
        // INPUT
        fun publishMessage(msg: UIMessage)

        // OUTOUT
        fun observeMessage(): Observable<UIMessage>
    }
}
