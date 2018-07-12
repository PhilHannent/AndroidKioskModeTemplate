package fr.afaucogney.mobile.android.accidentcounter.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

class AccidentEntity(@PrimaryKey val date: Long) : RealmObject()