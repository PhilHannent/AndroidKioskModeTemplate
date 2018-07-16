package fr.afaucogney.mobile.android.accidentcounter.domain

import com.vicpin.krealmextensions.queryAllAsFlowable
import fr.afaucogney.mobile.android.accidentcounter.common.archi.utils.LogUtil
import fr.afaucogney.mobile.android.accidentcounter.data.AccidentEntity
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ObserveLatestAccidentUseCase @Inject constructor() {

    fun execute(): Observable<Long> {
        return AccidentEntity()
                .queryAllAsFlowable()
                .map { it.sortedByDescending { it.date } }
                .toObservable()
                .map { it.first() }
                .map { it.date }
                .doOnNext { LogUtil.d(this, "onNext$it") }
                .subscribeOn(Schedulers.io())
    }
}