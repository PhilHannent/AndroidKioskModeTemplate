package wenchao.kiosk

import android.app.Application
import net.danlew.android.joda.JodaTimeAndroid

class KioskApp : Application() {

    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this);
    }
}