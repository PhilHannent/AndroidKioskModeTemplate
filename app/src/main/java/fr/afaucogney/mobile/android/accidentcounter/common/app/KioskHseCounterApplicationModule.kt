package fr.afaucogney.mobile.android.accidentcounter.common.app

import android.app.Application
import android.app.admin.DevicePolicyManager
import android.content.Context.DEVICE_POLICY_SERVICE
import toothpick.config.Module
import toothpick.smoothie.provider.SystemServiceProvider

class KioskHseCounterApplicationModule(application: Application) : Module() {
    init {
        // Libs
//        bind(Prefser::class.java).toProviderInstance(PrefserProvider(app)).providesSingletonInScope()
        bindSystemService<DevicePolicyManager>(application, DevicePolicyManager::class.java, DEVICE_POLICY_SERVICE)
    }

    private fun <T> bindSystemService(application: Application, serviceClass: Class<T>, serviceName: String) {
        bind(serviceClass).toProviderInstance(SystemServiceProvider(application, serviceName))
    }

}