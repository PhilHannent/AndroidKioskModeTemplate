package fr.afaucogney.mobile.android.accidentcounter.domain.counter

import com.vicpin.krealmextensions.queryAllAsFlowable
import fr.afaucogney.mobile.android.accidentcounter.data.AccidentEntity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

class ObserveRecordDayUseCase @Inject constructor() {


    fun execute(): Observable<Int> {
        return AccidentEntity()
                .queryAllAsFlowable()
                .map { it.sortedBy { it.date } }
                .toObservable()
                .subscribeOn(Schedulers.io())
                .map { updateRecord(it.map { it.date }) }
                .map { it.sorted().last() }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { t -> t.printStackTrace() }
                .retry()
    }

    private fun updateRecord(accidents: List<Long>): List<Int> {
        return when {
            accidents.isEmpty() -> listOf(-1)
            accidents.size == 1 -> listOf(DateTime((DateTime.now().millis - Date(accidents[0]).time)).dayTotal)
            else -> {
                val result: ArrayList<Int> = arrayListOf()
                val origin = accidents.toMutableList()
                origin.add(DateTime.now().millis)
                origin.forEachIndexed { index, date ->
                    if (index != 0) {
                        result.add(DateTime(date - Date(origin[index - 1]).time).dayTotal)//dayOfYear - 1)
                    }
                }
                result.toList()
            }
        }
    }

    private val DateTime.dayTotal: Int
        get() {
            return (dayOfYear - 1) + (365 * (year - 1970))
        }

}
