package fr.afaucogney.mobile.android.accidentcounter.domain

import com.github.pwittchen.prefser.library.rx2.Prefser
import fr.afaucogney.mobile.android.accidentcounter.common.archi.utils.LogUtil
import fr.afaucogney.mobile.android.accidentcounter.data.AccidentsList
import fr.afaucogney.mobile.android.accidentcounter.data.Constants
import io.reactivex.Observable
import javax.inject.Inject

class ObserveLatestAccidentUseCase @Inject constructor() {

    @Inject
    lateinit var prefser: Prefser

    fun execute(): Observable<Long> {
        LogUtil.d("PrefSer", prefser.toString())
        return prefser
                .getAndObserve(Constants.ACCIDENTS_KEY, AccidentsList, listOf())
                .flatMap { Observable.just(it.min()) }
    }
}