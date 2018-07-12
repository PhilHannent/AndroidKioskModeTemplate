package fr.afaucogney.mobile.android.accidentcounter.common.archi.rx

import fr.afaucogney.mobile.android.accidentcounter.common.archi.utils.LogUtil
import io.reactivex.observers.DisposableObserver

open class RxLogSubscriber<T>(var tag: String) : DisposableObserver<T>() {

    override fun onStart() {
        LogUtil.i(tag, "$tag onStart")
    }

    override fun onNext(t: T) {
        LogUtil.i(tag, "$tag onNext with :" + t?.toString())
    }

    override fun onComplete() {
        LogUtil.i(tag, "$tag onComplete")
    }

    override fun onError(e: Throwable) {
        LogUtil.e(tag, "$tag onError with :" + e.toString())
    }
}
