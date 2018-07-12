package fr.afaucogney.mobile.android.accidentcounter.domain

import com.github.pwittchen.prefser.library.rx2.Prefser
import fr.afaucogney.mobile.android.accidentcounter.common.archi.utils.LogUtil
import fr.afaucogney.mobile.android.accidentcounter.data.AccidentsList
import fr.afaucogney.mobile.android.accidentcounter.data.Constants
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ObserveAccidentsUseCase @Inject constructor() {

    @Inject
    lateinit var prefser: Prefser


    fun execute(): Observable<List<Long>> {
        LogUtil.d("PrefSer", prefser.toString())
        return prefser
                .observePreferences()
                .subscribeOn(Schedulers.io())
                .map { prefser.get(Constants.ACCIDENTS_KEY, AccidentsList, listOf(-1L)) }
                .observeOn(AndroidSchedulers.mainThread())
//         .filter {  }

//            .getAndObserve(Constants.ACCIDENTS_KEY, AccidentsList, listOf(-1L))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { LogUtil.d(this, "onNext" + it.toString()) }
//        LogUtil.d(this, result.toString())
//        return result
    }
}