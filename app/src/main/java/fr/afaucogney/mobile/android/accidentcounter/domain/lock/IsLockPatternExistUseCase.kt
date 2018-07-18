package fr.afaucogney.mobile.android.accidentcounter.domain.lock

import com.vicpin.krealmextensions.queryAsFlowable
import fr.afaucogney.mobile.android.accidentcounter.data.LockPatternEntity
import io.reactivex.Observable
import javax.inject.Inject


class IsLockPatternExistUseCase @Inject constructor() {
    fun execute(): Observable<Boolean> {
        return LockPatternEntity()
                .queryAsFlowable {}
                .map { it.isNotEmpty() }
                .toObservable()
    }
}