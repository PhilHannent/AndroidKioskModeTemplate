package fr.afaucogney.mobile.android.accidentcounter.common.archi.base

import android.app.Application
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.ViewModel
import android.support.annotation.CallSuper

import com.uber.autodispose.LifecycleEndedException
import com.uber.autodispose.LifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import toothpick.Toothpick
import toothpick.config.Module
import fr.afaucogney.mobile.android.accidentcounter.common.archi.UIMessage
import fr.afaucogney.mobile.android.accidentcounter.common.archi.utils.LogUtil


abstract class BaseViewModel(app: Application) : LifecycleObserver, BaseActivityContract.ViewModel, LifecycleScopeProvider<ViewModelLifeCycleEvent>, ViewModel() {

    ///////////////////////////////////////////////////////////////////////////
    // COMPANION
    ///////////////////////////////////////////////////////////////////////////

    companion object {
        private val LOG_TAG = BaseViewModel::class.java.simpleName
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // DATA
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    protected var mDispos: CompositeDisposable = CompositeDisposable()
    private val lifecycleSubject: BehaviorSubject<ViewModelLifeCycleEvent> = BehaviorSubject.create()
    protected val messageSubject: PublishSubject<UIMessage> = PublishSubject.create()

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // LIVE DATA
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // DEPENDENCIES
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    protected open val viewModelInjectionModule: Module = EmptyModule()

//    @Inject
//    lateinit var mRes: Resources

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    init {
        // TODO fix warnings
        LogUtil.i(LOG_TAG, "base onCreated")
        val viewModelScope = Toothpick.openScopes(app, this)
        viewModelScope.installModules(BaseViewModelModule(), viewModelInjectionModule)
        Toothpick.inject(this, viewModelScope)
        lifecycleSubject.onNext(ViewModelLifeCycleEvent.ON_CREATED)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // LIFE CYCLE
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @CallSuper
    override fun onCleared() {
        LogUtil.i(LOG_TAG, "onCleared")
        mDispos.clear()
        lifecycleSubject.onNext(ViewModelLifeCycleEvent.ON_CLEARED)
        Toothpick.closeScope(this)
        super.onCleared()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // SPECIALISATION
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun observeMessage(): Observable<UIMessage> {
        return messageSubject
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun publishMessage(msg: UIMessage) {
        LogUtil.d(LOG_TAG, "publishMessage")
        messageSubject.onNext(msg)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // AUTODISPOSE
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun lifecycle(): Observable<ViewModelLifeCycleEvent> {
        return lifecycleSubject.hide()
    }

    override fun peekLifecycle(): ViewModelLifeCycleEvent? {
        return lifecycleSubject.value
    }

    override fun correspondingEvents(): Function<ViewModelLifeCycleEvent, ViewModelLifeCycleEvent> {
        return Function { testLifecycle ->
            when (testLifecycle) {
                ViewModelLifeCycleEvent.ON_CREATED -> ViewModelLifeCycleEvent.ON_CLEARED
                ViewModelLifeCycleEvent.ON_CLEARED -> throw LifecycleEndedException()
            }
        }
    }
}
