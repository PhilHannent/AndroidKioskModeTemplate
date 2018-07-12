package fr.afaucogney.mobile.android.accidentcounter.common.app

import android.app.Application
import com.github.pwittchen.prefser.library.rx2.Prefser
import javax.inject.Provider

class PrefserProvider(var app: Application) : Provider<Prefser> {

    override fun get(): Prefser {
        return Prefser(app)
    }
}