package fr.afaucogney.mobile.android.accidentcounter.domain.counter

import com.vicpin.krealmextensions.deleteAll
import fr.afaucogney.mobile.android.accidentcounter.data.AccidentEntity
import javax.inject.Inject

class ClearAllAccidentsUseCase @Inject constructor() {

    fun execute() {
        AccidentEntity().deleteAll()
    }
}