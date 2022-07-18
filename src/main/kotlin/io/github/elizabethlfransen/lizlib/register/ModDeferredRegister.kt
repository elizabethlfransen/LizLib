package io.github.elizabethlfransen.lizlib.register

import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.minecraftforge.registries.RegistryBuilder
import net.minecraftforge.registries.RegistryObject
import thedarkcolour.kotlinforforge.forge.registerObject
import java.util.function.Supplier
import kotlin.properties.ReadOnlyProperty

class ModDeferredRegister<T>(val registerHandle: DeferredRegister<T>) : IDeferredRegister<T> {
    override fun <I : T> register(name: String, sup: () -> I): RegistryObject<I> =
        registerHandle.register(name, sup)

    override fun <E : IForgeRegistryEntry<E>> makeRegistry(
        base: Class<E>,
        sup: () -> RegistryBuilder<E>
    ): () -> IForgeRegistry<E> =
        registerHandle.makeRegistry(base, sup)::get

    override fun createTagKey(path: String): TagKey<T> =
        registerHandle.createTagKey(path)

    override fun createTagKey(location: ResourceLocation): TagKey<T> =
        registerHandle.createTagKey(location)

    override fun createOptionalTagKey(path: String, defaults: Set<() -> T>): TagKey<T> =
        registerHandle.createOptionalTagKey(
            path,
            defaults
                .map { Supplier(it) }
                .toSet()
        )

    override fun createOptionalTagKey(location: ResourceLocation, defaults: Set<() -> T>): TagKey<T> =
        registerHandle.createOptionalTagKey(
            location,
            defaults
                .map { Supplier(it) }
                .toSet()
        )

    override fun addOptionalTagDefaults(name: TagKey<T>, defaults: Set<() -> T>) =
        registerHandle.addOptionalTagDefaults(
            name,
            defaults
                .map { Supplier(it) }
                .toSet()
        )

    override fun register(bus: IEventBus) =
        registerHandle.register(bus)

    override val entries: Collection<RegistryObject<T>>
        get() = registerHandle.entries
    override val registryName: ResourceLocation?
        get() = registerHandle.registryName

    override fun registerObject(name: String, supplier: () -> T): ReadOnlyProperty<Any?, T> =
        registerHandle.registerObject(name, supplier)
}
