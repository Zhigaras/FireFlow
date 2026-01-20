package com.zhigaras.fireflow.model

sealed class FireFlowException(msg: String? = null) : RuntimeException(msg) {
    sealed class Fatal(msg: String? = null) : FireFlowException(msg)
    sealed class NonFatal(msg: String? = null) : FireFlowException(msg)
}

class OperationFailed : FireFlowException.NonFatal("The server indicated that this operation failed")
class Disconnected : FireFlowException.NonFatal("The operation had to be aborted due to a network disconnect")
class OverriddenBySet : FireFlowException.NonFatal("The transaction was overridden by a subsequent set")
class NetworkError : FireFlowException.NonFatal("The operation could not be performed due to a network error.")
class WriteCanceled : FireFlowException.NonFatal("The write was canceled locally")

class PermissionDenied: FireFlowException.Fatal("This client does not have permission to perform this operation")
class ExpiredToken : FireFlowException.Fatal("The supplied auth token has expired")
class InvalidToken : FireFlowException.Fatal("The specified authentication token is invalid. This can occur when the token is malformed, expired, or the secret that was used to generate it has been revoked.")
class MaxRetries : FireFlowException.Fatal("The transaction had too many retries")
class Unavailable : FireFlowException.Fatal("The service is unavailable")
class Unknown : FireFlowException.Fatal("Unknown exception")