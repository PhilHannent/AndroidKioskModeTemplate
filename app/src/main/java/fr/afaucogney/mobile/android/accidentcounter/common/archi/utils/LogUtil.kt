@file:Suppress("NOTHING_TO_INLINE")

package fr.afaucogney.mobile.android.accidentcounter.common.archi.utils

import android.util.Log
import io.reactivex.Observable

object LogUtil {
    fun d(context: String, msg: String) {
        Log.d(context, msg)
    }

    fun d(context: Any, msg: String) {
        Log.d(context.javaClass.canonicalName, msg)
    }

    fun i(context: String, msg: String) {
        Log.i(context, msg)
    }

    fun e(context: String, msg: String) {
        Log.e(context, msg)
    }


    fun i(context: Any, msg: String) {
        Log.i(context.javaClass.canonicalName, msg)
    }

    fun e(context: Any, msg: String) {
        Log.e(context.javaClass.canonicalName, msg)
    }
}

inline fun <T> Observable<T>.doOnNextLog(): Observable<T> {
    return this.doOnNext { LogUtil.i(this.javaClass.simpleName, "onNext : $this") }
}

inline fun <T> Observable<T>.doOnSubscribeLog(): Observable<T> {
    return this.doOnSubscribe { LogUtil.i(this.javaClass.simpleName, "onSubscribe : $this") }
}

inline fun <T> Observable<T>.doOnCompleteLog(): Observable<T> {
    return this.doOnComplete { LogUtil.i(this.javaClass.simpleName, "onComplete : $this") }
}
