package com.zhigaras.fireflow.mapper

import com.google.firebase.database.DatabaseError
import com.zhigaras.fireflow.model.Disconnected
import com.zhigaras.fireflow.model.ExpiredToken
import com.zhigaras.fireflow.model.InvalidToken
import com.zhigaras.fireflow.model.MaxRetries
import com.zhigaras.fireflow.model.NetworkError
import com.zhigaras.fireflow.model.OperationFailed
import com.zhigaras.fireflow.model.OverriddenBySet
import com.zhigaras.fireflow.model.PermissionDenied
import com.zhigaras.fireflow.model.Unavailable
import com.zhigaras.fireflow.model.Unknown
import com.zhigaras.fireflow.model.WriteCanceled

internal class FireFlowExceptionMapper {
    fun mapFromDatabaseError(code: Int) = when (code) {
        DatabaseError.OPERATION_FAILED -> OperationFailed()
        DatabaseError.PERMISSION_DENIED -> PermissionDenied()
        DatabaseError.DISCONNECTED -> Disconnected()
        DatabaseError.EXPIRED_TOKEN -> ExpiredToken()
        DatabaseError.INVALID_TOKEN -> InvalidToken()
        DatabaseError.MAX_RETRIES -> MaxRetries()
        DatabaseError.OVERRIDDEN_BY_SET -> OverriddenBySet()
        DatabaseError.UNAVAILABLE -> Unavailable()
        DatabaseError.NETWORK_ERROR -> NetworkError()
        DatabaseError.WRITE_CANCELED -> WriteCanceled()
        else -> Unknown()
    }
}
