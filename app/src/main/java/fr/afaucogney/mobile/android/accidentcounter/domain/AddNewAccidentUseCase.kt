package fr.afaucogney.mobile.android.accidentcounter.domain

import com.vicpin.krealmextensions.save
import fr.afaucogney.mobile.android.accidentcounter.data.AccidentEntity
import org.joda.time.DateTime
import javax.inject.Inject


class AddNewAccidentUseCase @Inject constructor() {

    fun execute(accident: DateTime) {
        AccidentEntity(accident).save()
    }
}