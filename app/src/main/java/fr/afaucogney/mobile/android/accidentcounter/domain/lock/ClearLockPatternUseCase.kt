package fr.afaucogney.mobile.android.accidentcounter.domain.lock

import com.vicpin.krealmextensions.deleteAll
import fr.afaucogney.mobile.android.accidentcounter.data.Constants.SINGLE
import fr.afaucogney.mobile.android.accidentcounter.data.LockPatternEntity
import javax.inject.Inject

class ClearLockPatternUseCase @Inject constructor() {

    fun execute() {
        LockPatternEntity(SINGLE).deleteAll()
    }
}