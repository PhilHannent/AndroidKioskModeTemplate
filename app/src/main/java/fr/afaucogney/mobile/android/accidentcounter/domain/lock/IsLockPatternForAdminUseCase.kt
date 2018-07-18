package fr.afaucogney.mobile.android.accidentcounter.domain.lock

import com.vicpin.krealmextensions.queryFirst
import fr.afaucogney.mobile.android.accidentcounter.data.LockPatternEntity
import io.reactivex.Observable
import javax.inject.Inject

class IsLockPatternForAdminUseCase @Inject constructor() {
    fun execute(pattern: String): Observable<Boolean> {
        return Observable.just(LockPatternEntity().queryFirst()?.pattern.equals(pattern))
    }

}