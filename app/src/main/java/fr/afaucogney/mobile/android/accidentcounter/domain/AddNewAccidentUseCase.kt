package fr.afaucogney.mobile.android.accidentcounter.domain

import com.github.pwittchen.prefser.library.rx2.Prefser
import com.vicpin.krealmextensions.save
import fr.afaucogney.mobile.android.accidentcounter.common.archi.utils.LogUtil
import fr.afaucogney.mobile.android.accidentcounter.data.AccidentEntity
import fr.afaucogney.mobile.android.accidentcounter.data.AccidentsList
import fr.afaucogney.mobile.android.accidentcounter.data.Constants.ACCIDENTS_KEY
import org.joda.time.DateTime
import javax.inject.Inject


class AddNewAccidentUseCase @Inject constructor() {

    @Inject
    lateinit var prefser: Prefser

    fun execute(accident: DateTime) {
        LogUtil.d("AddNewAccidentUseCase", accident.toString())


        AccidentEntity(accident.millis).save()
        LogUtil.d("PrefSer", prefser.toString())
        prefser.put(ACCIDENTS_KEY, prefser.get(ACCIDENTS_KEY, AccidentsList, listOf()).plus(accident.millis), AccidentsList)
    }

}