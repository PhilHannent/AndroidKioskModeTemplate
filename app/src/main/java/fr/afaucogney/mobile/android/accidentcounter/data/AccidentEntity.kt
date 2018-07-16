package fr.afaucogney.mobile.android.accidentcounter.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

open class AccidentEntity() : RealmObject() {

    constructor(date: DateTime) : this() {
        this.date = date.millis
        this.readableDate = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(date.millis)
    }

    @PrimaryKey
    var date: Long = -1
    var readableDate: String = "1979-06-20"

}