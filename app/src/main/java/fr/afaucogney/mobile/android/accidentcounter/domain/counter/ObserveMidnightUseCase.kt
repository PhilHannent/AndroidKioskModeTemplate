package fr.afaucogney.mobile.android.accidentcounter.domain.counter

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import org.joda.time.DateTime
import javax.inject.Inject


class ObserveMidnightUseCase @Inject constructor() {

    @Inject
    lateinit var alarmManager: AlarmManager

    @Inject
    lateinit var application: Application

    var dateSubject: PublishSubject<DateTime> = PublishSubject.create()

    inner class MidnightBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            dateSubject.onNext(DateTime.now())
        }
    }

    fun execute(): Observer<DateTime> {
        val midnightPI = PendingIntent.getService(application, 0, Intent(application, ObserveMidnightUseCase.MidnightBroadcastReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, DateTime.now().withTimeAtStartOfDay().millis, AlarmManager.INTERVAL_DAY, midnightPI)
        return dateSubject
    }
}