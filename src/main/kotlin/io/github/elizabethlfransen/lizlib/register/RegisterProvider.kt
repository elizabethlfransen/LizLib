package io.github.elizabethlfransen.lizlib.register

typealias RegisterProvider<T> = (modId: String, registerFactory: IDeferredRegisterFactory) -> IDeferredRegister<T>
