package io.github.elizabethlfransen.lizlib.register

import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import java.util.function.Supplier

class RegisterHelperBuilder {
    private val registerProviders = mutableMapOf<Class<*>, RegisterProvider<*>>()

    fun <T> addRegister(type: Class<T>, register: RegisterProvider<T>) {
        registerProviders[type] = register
    }

    inline fun <reified T> addRegister(noinline register: RegisterProvider<T>)
        = addRegister(T::class.java, register)

    inline fun <reified T: IForgeRegistryEntry<T>> addForgeRegister(registry: IForgeRegistry<T>) =
        addRegister(T::class.java) { modId ->
            DeferredRegister.create(registry, modId)
        }

    inline fun <reified T : IForgeRegistryEntry<T>> addForgeRegister(supplier: Supplier<IForgeRegistry<T>>) =
        addRegister(T::class.java) {modId ->
            DeferredRegister.create(supplier.get(), modId)
        }



    fun addForgeRegisters() {
        addForgeRegister(ForgeRegistries.BLOCKS)
        addForgeRegister(ForgeRegistries.FLUIDS)
        addForgeRegister(ForgeRegistries.ITEMS)
        addForgeRegister(ForgeRegistries.MOB_EFFECTS)
        addForgeRegister(ForgeRegistries.SOUND_EVENTS)
        addForgeRegister(ForgeRegistries.POTIONS)
        addForgeRegister(ForgeRegistries.ENCHANTMENTS)
        addForgeRegister(ForgeRegistries.ENTITIES)
        addForgeRegister(ForgeRegistries.BLOCK_ENTITIES)
        addForgeRegister(ForgeRegistries.PARTICLE_TYPES)
        addForgeRegister(ForgeRegistries.CONTAINERS)
        addForgeRegister(ForgeRegistries.PAINTING_TYPES)
        addForgeRegister(ForgeRegistries.RECIPE_SERIALIZERS)
        addForgeRegister(ForgeRegistries.ATTRIBUTES)
        addForgeRegister(ForgeRegistries.STAT_TYPES)
        addForgeRegister(ForgeRegistries.PROFESSIONS)
        addForgeRegister(ForgeRegistries.POI_TYPES)
        addForgeRegister(ForgeRegistries.MEMORY_MODULE_TYPES)
        addForgeRegister(ForgeRegistries.SENSOR_TYPES)
        addForgeRegister(ForgeRegistries.SCHEDULES)
        addForgeRegister(ForgeRegistries.ACTIVITIES)
        addForgeRegister(ForgeRegistries.WORLD_CARVERS)
        addForgeRegister(ForgeRegistries.FEATURES)
        addForgeRegister(ForgeRegistries.CHUNK_STATUS)
        addForgeRegister(ForgeRegistries.STRUCTURE_FEATURES)
        addForgeRegister(ForgeRegistries.BLOCK_STATE_PROVIDER_TYPES)
        addForgeRegister(ForgeRegistries.FOLIAGE_PLACER_TYPES)
        addForgeRegister(ForgeRegistries.TREE_DECORATOR_TYPES)
        addForgeRegister(ForgeRegistries.BIOMES)
        addForgeRegister(ForgeRegistries.DATA_SERIALIZERS)
        addForgeRegister(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS)
        addForgeRegister(ForgeRegistries.WORLD_TYPES)
    }

    fun build(modId: String): RegisterHelper {
        return RegisterHelper(
            modId,
            registerProviders
        )
    }
}

fun registerHelper(modId: String, init: RegisterHelperBuilder.() -> Unit): RegisterHelper
{
    return RegisterHelperBuilder()
        .apply(init)
        .build(modId)
}