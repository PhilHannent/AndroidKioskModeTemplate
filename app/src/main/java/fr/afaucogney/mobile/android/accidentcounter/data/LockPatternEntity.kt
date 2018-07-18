package fr.afaucogney.mobile.android.accidentcounter.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class LockPatternEntity() : RealmObject() {

    constructor(lockPattern: String) : this() {
        this.pattern = lockPattern
    }

    @Required
    @PrimaryKey
    var single = Constants.SINGLE
    var pattern: String = "NA"
}