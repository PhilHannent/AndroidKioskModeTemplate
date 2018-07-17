package fr.afaucogney.mobile.android.accidentcounter.domain.counter

import com.vicpin.krealmextensions.queryAllAsFlowable
import fr.afaucogney.mobile.android.accidentcounter.common.archi.utils.LogUtil
import fr.afaucogney.mobile.android.accidentcounter.data.AccidentEntity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ObserveAccidentsUseCase @Inject constructor() {

    fun execute(): Observable<List<Long>> {
        return AccidentEntity()
                .queryAllAsFlowable()
                .map { it.sortedBy { it.date } }
                .toObservable()
                .subscribeOn(Schedulers.io())
                .doOnNext { LogUtil.d(this, "onNext" + it.map { it.readableDate }) }
                .map { it.map { it.date } }
                .observeOn(AndroidSchedulers.mainThread())
    }
}