package fr.afaucogney.mobile.android.accidentcounter.common.archi.base

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import javax.inject.Provider

class LifecycleRegistryProvider(var activity: LifecycleOwner) : Provider<LifecycleRegistry> {
    override fun get(): LifecycleRegistry {
        return LifecycleRegistry(activity)
    }
}
