package fr.afaucogney.mobile.android.accidentcounter

import android.app.Application
import com.facebook.stetho.Stetho
import fr.afaucogney.mobile.android.accidentcounter.common.app.KioskHseCounterApplicationModule
import io.realm.Realm
import io.realm.RealmConfiguration
import net.danlew.android.joda.JodaTimeAndroid
import toothpick.Toothpick
import toothpick.configuration.Configuration
import toothpick.registries.FactoryRegistryLocator
import toothpick.registries.MemberInjectorRegistryLocator
import toothpick.smoothie.module.SmoothieApplicationModule

class KioskApp : Application() {

    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
        setupToothPick()
        setupRealm()
        Stetho.initializeWithDefaults(this)
    }

    private fun setupToothPick() {

        // com.example.smoothie.MemberInjectorRegistry and com.example.smoothie.FactoryRegistry are classes generated by this project
        // Your project will have these classes in your package (or the one you specify).
        // Please note that the fully qualified name should be used instead of an import.
        // (see https://github.com/stephanenicolas/toothpick/wiki/Factory-and-Member-Injector-registries)
        // If you're not using the reflection free configuration, the next 3 lines can be omitted
        Toothpick.setConfiguration(Configuration.forProduction().disableReflection())
        MemberInjectorRegistryLocator.setRootRegistry(fr.afaucogney.mobile.android.accidentcounter.MemberInjectorRegistry())
        FactoryRegistryLocator.setRootRegistry(fr.afaucogney.mobile.android.accidentcounter.FactoryRegistry())

        val appScope = Toothpick.openScope(this)
        appScope.installModules(
                SmoothieApplicationModule(this), KioskHseCounterApplicationModule(this)
        )
    }


    private fun setupRealm() {
        // Initialize Realm
        Realm.init(this)
        val realmConfBuilder = RealmConfiguration.Builder()
                .name("accidentCounter.realm")
                .schemaVersion(1)

        Realm.setDefaultConfiguration(realmConfBuilder.build())
    }

}