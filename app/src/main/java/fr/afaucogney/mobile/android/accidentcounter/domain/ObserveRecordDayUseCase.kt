package fr.afaucogney.mobile.android.accidentcounter.domain

import com.github.pwittchen.prefser.library.rx2.Prefser
import fr.afaucogney.mobile.android.accidentcounter.common.archi.utils.LogUtil
import fr.afaucogney.mobile.android.accidentcounter.data.AccidentsList
import fr.afaucogney.mobile.android.accidentcounter.data.Constants
import io.reactivex.Observable
import org.joda.time.DateTime
import javax.inject.Inject

class ObserveRecordDayUseCase @Inject constructor() {

    @Inject
    lateinit var prefser: Prefser


    fun execute(): Observable<List<Int>> {
        LogUtil.d("PrefSer", prefser.toString())
        return prefser.getAndObserve(Constants.ACCIDENTS_KEY, AccidentsList, listOf()).flatMap { updateRecord(it) }
    }


    private fun updateRecord(accidents: List<Long>): Observable<List<Int>> {
        return if (accidents.isEmpty()) {
            Observable.just(listOf(-1))
        } else if (accidents.size == 1) {
            Observable.just(listOf(DateTime.now().minus(DateTime.parse(accidents[0].toString()).millis).dayOfYear))
        } else {
            val result: ArrayList<Int> = arrayListOf()
            val origin = accidents.toMutableList()
            origin.add(DateTime.now().millis)
            origin.forEachIndexed { index, date ->
                if (index != 0) {
                    result.add(DateTime.parse((date - origin[index - 1]).toString()).dayOfYear)
                }
            }
            Observable.just(result.toList())
        }
    }

}