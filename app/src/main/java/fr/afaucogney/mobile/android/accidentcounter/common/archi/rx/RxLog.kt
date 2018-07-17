package fr.afaucogney.mobile.android.accidentcounter.common.archi.rx

import android.util.Log
import io.reactivex.Observable

fun <T> Observable<T>.logAllSubscriptionEvents(context: String): Observable<T> {
    return this
            .doOnSubscribe { Log.w(context, "onSubscribe") }
            .doOnComplete { Log.w(context, "onComplete") }
            .doOnDispose { Log.w(context, "onDispose") }
            .doOnError { Log.w(context, "onError") }
            .doOnEach { Log.w(context, "onEach") }
            .doOnTerminate { Log.w(context, "onTerminate") }
}
