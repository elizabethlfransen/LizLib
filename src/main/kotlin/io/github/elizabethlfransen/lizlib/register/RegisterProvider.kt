package io.github.elizabethlfransen.lizlib.register

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.registries.DeferredRegister

typealias RegisterProvider<T> = (modId: String, registerFactory: IDeferredRegisterFactory) -> IDeferredRegister<T>