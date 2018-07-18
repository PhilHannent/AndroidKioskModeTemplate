package fr.afaucogney.mobile.android.accidentcounter.domain.lock

import com.vicpin.krealmextensions.save
import fr.afaucogney.mobile.android.accidentcounter.data.LockPatternEntity
import javax.inject.Inject

class AddNewLockPatternUseCase @Inject constructor() {

    fun execute(pattern: String) {
        LockPatternEntity(pattern).save()
    }
}