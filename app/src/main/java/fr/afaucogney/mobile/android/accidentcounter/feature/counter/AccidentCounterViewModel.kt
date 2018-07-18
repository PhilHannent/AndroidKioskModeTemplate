package fr.afaucogney.mobile.android.accidentcounter.feature.counter

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.uber.autodispose.kotlin.autoDisposable
import fr.afaucogney.mobile.android.accidentcounter.common.archi.base.BaseViewModel
import fr.afaucogney.mobile.android.accidentcounter.common.archi.rx.RxLogSubscriber
import fr.afaucogney.mobile.android.accidentcounter.common.archi.rx.logAllSubscriptionEvents
import fr.afaucogney.mobile.android.accidentcounter.data.LockPatternDialogEvent
import fr.afaucogney.mobile.android.accidentcounter.domain.counter.*
import fr.afaucogney.mobile.android.accidentcounter.domain.lock.AddNewLockPatternUseCase
import fr.afaucogney.mobile.android.accidentcounter.domain.lock.ClearLockPatternUseCase
import fr.afaucogney.mobile.android.accidentcounter.domain.lock.IsLockPatternExistUseCase
import fr.afaucogney.mobile.android.accidentcounter.domain.lock.IsLockPatternForAdminUseCase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import javax.inject.Inject

class AccidentCounterViewModel @Inject constructor(app: Application) : BaseViewModel(app), AccidentCounterContract.ViewModel {

    @Inject
    internal lateinit var removeAccidentUseCase: RemoveAccidentUseCase

    @Inject
    internal lateinit var clearAllAccidentsUseCase: ClearAllAccidentsUseCase

    @Inject
    internal lateinit var observeRecordDayUseCase: ObserveRecordDayUseCase

    @Inject
    internal lateinit var addNewAccidentUseCase: AddNewAccidentUseCase

    @Inject
    internal lateinit var observeAccidentsUseCase: ObserveAccidentsUseCase

    @Inject
    internal lateinit var observeLatestAccidentUseCase: ObserveLatestAccidentUseCase

    @Inject
    internal lateinit var observeMidnightUseCase: ObserveMidnightUseCase

    @Inject
    internal lateinit var clearLockPatternUseCase: ClearLockPatternUseCase

    @Inject
    internal lateinit var addNewLockPatternUseCase: AddNewLockPatternUseCase

    @Inject
    internal lateinit var isLockPatternForAdminUseCase: IsLockPatternForAdminUseCase

    @Inject
    internal lateinit var isLockPatternExistUseCase: IsLockPatternExistUseCase


    private val accidents: MutableLiveData<List<Long>> = MutableLiveData()
    private val record: MutableLiveData<String> = MutableLiveData()
    private val latestAccident: MutableLiveData<String> = MutableLiveData()
    private val current: MutableLiveData<String> = MutableLiveData()
    private val kioskMode: MutableLiveData<Boolean> = MutableLiveData()
    private val lockPatternDialogVisibility: MutableLiveData<LockPatternDialogEvent> = MutableLiveData()

    private val lockPatternSubject: PublishSubject<String> = PublishSubject.create()
    private val symbolSubject: PublishSubject<Long> = PublishSubject.create()

    init {
        observeAccidentsUseCase
                .execute()
                .doOnNext { accidents.value = it }
                .logAllSubscriptionEvents("accident")
                .autoDisposable(this)
                .subscribe(RxLogSubscriber("accidents"))

        observeRecordDayUseCase
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { record.value = it.toString() }
                .logAllSubscriptionEvents("record")
                .autoDisposable(this)
                .subscribe(RxLogSubscriber("record"))

        observeLatestAccidentUseCase
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { latestAccident.value = SimpleDateFormat("dd MM yyyy").format(it) }
                .doOnNext { current.value = (DateTime.now().minus(it).dayOfYear - 1).toString() }
                .onErrorResumeNext { t: Throwable ->
                    if (t is NoSuchElementException) {
                        latestAccident.value = "NA"
                        current.value = "NA"
                        t.printStackTrace()
                        Observable.empty()
                    } else {
                        Observable.error(t)
                    }
                }
                .logAllSubscriptionEvents("latest")
                .autoDisposable(this)
                .subscribe(RxLogSubscriber("latest"))

        lockPatternSubject
                .switchMap { isLockPatternForAdminUseCase.execute(it) }
                .doOnNext { kioskMode.value = it }
                .doOnNext {
                    if (it) {
                        lockPatternDialogVisibility.value = LockPatternDialogEvent.UNLOCK
                    } else {
                        lockPatternDialogVisibility.value = LockPatternDialogEvent.LOCK
                    }
                }
                .logAllSubscriptionEvents("patternCheck")
                .autoDisposable(this)
                .subscribe(RxLogSubscriber("patternCheck"))

        Observable
                .combineLatest<Boolean, Long, Boolean>(
                        isLockPatternExistUseCase.execute(),
                        symbolSubject,
                        BiFunction { t1, _ -> t1 }
                )
                .doOnNext {
                    if (it) {
                        lockPatternDialogVisibility.value = LockPatternDialogEvent.LOCK
                    } else {
                        lockPatternDialogVisibility.value = LockPatternDialogEvent.SHOWRECORD
                    }
                }
                .logAllSubscriptionEvents("patternRecord")
                .autoDisposable(this)
                .subscribe(RxLogSubscriber("patternRecord"))
    }

    override fun observeAccidents() = accidents
    override fun observeDaysRecord() = record
    override fun observeLatestAccident() = latestAccident
    override fun observeDaysSinceLatestAccident() = current
    override fun observeLockPatternDialog() = lockPatternDialogVisibility
    override fun observeKioskMode() = kioskMode


    override fun addNewAccident(accident: DateTime) {
        addNewAccidentUseCase.execute(accident)
    }

    override fun clearAccidents() {
        clearAllAccidentsUseCase.execute()
    }

    override fun removeAccident(accident: Long) {
        removeAccidentUseCase.execute(accident)
    }


    override fun updateLockPattern(pattern: String) {
        addNewLockPatternUseCase.execute(pattern)
        lockPatternDialogVisibility.value = LockPatternDialogEvent.LOCK
    }

    override fun testLockPattern(pattern: String) {
        lockPatternSubject.onNext(pattern)
    }


    override fun tryToGoToKioskMode() {
        symbolSubject.onNext(DateTime.now().millis)
    }

    override fun clearLockPattern() {
        clearLockPatternUseCase.execute()
    }


}

