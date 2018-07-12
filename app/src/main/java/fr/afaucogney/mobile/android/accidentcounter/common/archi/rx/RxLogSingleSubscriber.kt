package fr.afaucogney.mobile.android.accidentcounter.common.archi.rx

import io.reactivex.observers.DisposableSingleObserver
import fr.afaucogney.mobile.android.accidentcounter.common.archi.utils.LogUtil

class RxLogSingleSubscriber<T>(var tag: String) : DisposableSingleObserver<T>() {

    override fun onStart() {
        super.onStart()
        LogUtil.i(tag, "onStart")
    }

    override fun onSuccess(t: T) {
        LogUtil.i(tag, "onNext with :" + t?.toString())
    }

    override fun onError(e: Throwable) {
        LogUtil.e(tag, "onError with :" + e.toString())
    }
}
