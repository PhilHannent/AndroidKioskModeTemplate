package fr.afaucogney.mobile.android.accidentcounter.common.app

import android.app.Application
import com.github.pwittchen.prefser.library.rx2.Prefser
import toothpick.config.Module
import toothpick.smoothie.provider.SystemServiceProvider

class KioskHseCounterApplicationModule(app: Application) : Module() {
    init {
        // Libs
        bind(Prefser::class.java).toProviderInstance(PrefserProvider(app)).providesSingletonInScope()
    }

    private fun <T> bindSystemService(application: Application, serviceClass: Class<T>, serviceName: String) {
        bind(serviceClass).toProviderInstance(SystemServiceProvider(application, serviceName))
    }

}