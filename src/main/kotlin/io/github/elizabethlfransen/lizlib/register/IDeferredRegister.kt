package io.github.elizabethlfransen.lizlib.register

import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.minecraftforge.registries.RegistryBuilder
import net.minecraftforge.registries.RegistryObject
import kotlin.properties.ReadOnlyProperty

interface IDeferredRegister<T> {

    fun <I : T> register(name: String, sup: () -> I): RegistryObject<I>

    fun <E : IForgeRegistryEntry<E>> makeRegistry(base: Class<E>, sup: () -> RegistryBuilder<E>): (() -> IForgeRegistry<E>)

    fun createTagKey(path: String): TagKey<T>

    fun createTagKey(location: ResourceLocation): TagKey<T>

    fun createOptionalTagKey(path: String, defaults: Set<() -> T>): TagKey<T>

    fun createOptionalTagKey(location: ResourceLocation, defaults: Set<() -> T>): TagKey<T>

    fun addOptionalTagDefaults(name: TagKey<T>, defaults: Set<() -> T>)

    fun register(bus: IEventBus)

    val entries: Collection<RegistryObject<T>>

    val registryName: ResourceLocation?

    fun registerObject(name: String, supplier: () -> T): ReadOnlyProperty<Any?, T>
}

inline fun <reified E : IForgeRegistryEntry<E>> IDeferredRegister<E>.makeRegistry(noinline sup: () -> RegistryBuilder<E>) =
    this.makeRegistry(E::class.java, sup)
