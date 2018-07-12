package fr.afaucogney.mobile.android.accidentcounter.common.archi.base

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.support.v7.app.AppCompatActivity
import toothpick.config.Module

class BaseActivityModule(activity: AppCompatActivity) : Module() {
    init {
        bind(LifecycleRegistry::class.java).toProviderInstance(LifecycleRegistryProvider(activity as LifecycleOwner))
    }
}
