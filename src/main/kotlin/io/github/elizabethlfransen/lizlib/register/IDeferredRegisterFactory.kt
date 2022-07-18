package io.github.elizabethlfransen.lizlib.register

import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry

interface IDeferredRegisterFactory {

    fun <E : IForgeRegistryEntry<E>> create(registry: IForgeRegistry<E>, modId: String): IDeferredRegister<E>
}
