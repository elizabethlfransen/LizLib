package io.github.elizabethlfransen.lizlib.register

import net.minecraftforge.registries.DeferredRegister

typealias RegisterProvider<T> = (modId: String) -> DeferredRegister<T>