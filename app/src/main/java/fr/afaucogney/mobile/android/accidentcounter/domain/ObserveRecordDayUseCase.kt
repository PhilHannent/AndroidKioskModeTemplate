package fr.afaucogney.mobile.android.accidentcounter.domain

import com.vicpin.krealmextensions.queryAllAsFlowable
import fr.afaucogney.mobile.android.accidentcounter.data.AccidentEntity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

class ObserveRecordDayUseCase @Inject constructor() {


    fun execute(): Observable<List<Int>> {
        return AccidentEntity()
                .queryAllAsFlowable()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .map { updateRecord(it.map { it.date }) }
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
    }


    private fun updateRecord(accidents: List<Long>): List<Int> {
        return when {
            accidents.isEmpty() -> listOf(-1)
            accidents.size == 1 -> listOf(DateTime.now().minus(DateTime.parse(accidents[0].toString()).millis).dayOfYear)
            else -> {
                val result: ArrayList<Int> = arrayListOf()
                val origin = accidents.toMutableList()
                origin.add(DateTime.now().millis)
                origin.forEachIndexed { index, date ->
                    if (index != 0) {
                        result.add(DateTime.parse(date.minus(origin[index - 1]).toString()).dayOfYear)
                    }
                }
                result.toList()
            }
        }
    }

}