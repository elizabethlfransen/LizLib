package io.github.elizabethlfransen.lizlib.register

import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry

class ModDeferredRegisterFactory : IDeferredRegisterFactory {
    override fun <E : IForgeRegistryEntry<E>> create(registry: IForgeRegistry<E>, modId: String): IDeferredRegister<E> {
        return ModDeferredRegister<E>(DeferredRegister.create(registry,modId))
    }
}