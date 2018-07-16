package fr.afaucogney.mobile.android.accidentcounter.domain

import com.vicpin.krealmextensions.delete
import fr.afaucogney.mobile.android.accidentcounter.data.AccidentEntity
import javax.inject.Inject

class RemoveAccidentUseCase @Inject constructor() {

    fun execute(date: Long) {
        AccidentEntity().delete { equalTo("date", date) }
    }
}