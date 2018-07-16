package fr.afaucogney.mobile.android.accidentcounter.feature

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.uber.autodispose.kotlin.autoDisposable
import fr.afaucogney.mobile.android.accidentcounter.common.archi.base.BaseViewModel
import fr.afaucogney.mobile.android.accidentcounter.common.archi.rx.RxLogSubscriber
import fr.afaucogney.mobile.android.accidentcounter.domain.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import javax.inject.Inject

class AccidentCounterViewModel @Inject constructor(app: Application) : BaseViewModel(app), AccidentCounterContract.ViewModel {

    @Inject
    lateinit var removeAccidentUseCase: RemoveAccidentUseCase

    @Inject
    lateinit var clearAllAccidentsUseCase: ClearAllAccidentsUseCase

    @Inject
    lateinit var observeRecordDayUseCase: ObserveRecordDayUseCase

    @Inject
    lateinit var observeAccidentsUseCase: ObserveAccidentsUseCase

    @Inject
    lateinit var addNewAccidentUseCase: AddNewAccidentUseCase

    @Inject
    lateinit var observeLatestAccidentUseCase: ObserveLatestAccidentUseCase


    val accidents: MutableLiveData<List<Long>> = MutableLiveData()
    val record: MutableLiveData<String> = MutableLiveData()
    val latestAccident: MutableLiveData<String> = MutableLiveData()
    val current: MutableLiveData<String> = MutableLiveData()
    val kioskMode: MutableLiveData<Boolean> = MutableLiveData()

    init {
        observeAccidentsUseCase
                .execute()
//                .filter { it.isEmpty() }
//                .doOnNextLog()
//                .map { it.map { SimpleDateFormat("dd MM yyyy").format(it) } }
//                .flatMap { Observable.just(it.map { SimpleDateFormat("dd MM yyyy").format(it) }) }
//                .flatMap { Observable.fromArray(it).map { SimpleDateFormat("dd MM yyyy").format(it) }.doOnNextLog().toList().toObservable() }
//                .map { it.mapTo()forEach {  it = SimpleDateFormat("dd MM yyyy").format(it)} }
//                .onErrorResumeNext { throwable: Throwable -> Observable.just(listOf<String>()) }
//                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { accidents.value = it }
                .logAllSubscriptionEvents("accident")
                .autoDisposable(this)
                .subscribeWith(RxLogSubscriber("accidents"))
//
        observeRecordDayUseCase
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { record.value = it.toString() }
                .logAllSubscriptionEvents("record")
                .autoDisposable(this)
                .subscribeWith(RxLogSubscriber("record"))

        observeLatestAccidentUseCase
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { latestAccident.value = SimpleDateFormat("dd MM yyyy").format(it) }
                .doOnNext { current.value = DateTime.now().minus(it).dayOfYear.toString() }
                .onErrorResumeNext { t: Throwable ->
                    if (t is NoSuchElementException) {
                        latestAccident.value = "NA"
                        current.value = "NA"
                        Observable.empty()
                    } else {
                        Observable.error(t)
                    }
                }
                .logAllSubscriptionEvents("latest")
                .autoDisposable(this)
                .subscribe(RxLogSubscriber("latest"))

        record.value = "-4"
    }


    override fun observeAccidents() = accidents
    override fun observeDaysRecord() = record
    override fun observeLatestAccident() = latestAccident
    override fun observeDaysSinceLatestAccident() = current

    override fun addNewAccident(accident: DateTime) {
        addNewAccidentUseCase.execute(accident)
    }

    override fun clearAccidents() {
        clearAllAccidentsUseCase.execute()
    }

    override fun removeAccident(accident: Long) {
        removeAccidentUseCase.execute(accident)
    }

    override fun updateLockPattern() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

private fun <T> Observable<T>.logAllSubscriptionEvents(context: String): Observable<T> {
    return this
            .doOnSubscribe { Log.w(context, "onSubscribe") }
            .doOnComplete { Log.w(context, "onComplete") }
            .doOnDispose { Log.w(context, "onDispose") }
            .doOnError { Log.w(context, "onError") }
            .doOnEach { Log.w(context, "onEach") }
            .doOnTerminate { Log.w(context, "onTerminate") }
}
