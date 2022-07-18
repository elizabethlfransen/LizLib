package io.github.elizabethlfransen.lizlib.register

import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import java.util.function.Supplier

/**
 * Builder for [RegisterHelper]
 */
class RegisterHelperBuilder {
    private val registerProviders = mutableMapOf<Class<*>, RegisterProvider<*>>()
    var registerFactory: IDeferredRegisterFactory = ModDeferredRegisterFactory()

    /**
     * Adds [register] of type [T] to be used by helper
     */
    fun <T> addRegister(type: Class<T>, register: RegisterProvider<T>) {
        registerProviders[type] = register
    }
    fun addForgeRegister(registry: IForgeRegistry<*>) {
        registerProviders[registry.registrySuperType] = { modId, registerFactory -> registerFactory.create(registry, modId) }
    }

    fun <T : IForgeRegistryEntry<T>> addForgeRegister(type: Class<T>, sup: Supplier<IForgeRegistry<T>>) {
        registerProviders[type] = {
            modId, registerFactory ->
            registerFactory.create(sup.get(), modId)
        }
    }
    /**
     * Adds a forge register for [registry] from [ForgeRegistries].
     * [registry] is lazily called when used by the helper.
     */
    inline fun <reified T : IForgeRegistryEntry<T>> addForgeRegister(sup: Supplier<IForgeRegistry<T>>) =
        addForgeRegister(T::class.java, sup)

    /**
     * Add [register] of type [T] to be used by helper
     */
    inline fun <reified T> addRegister(noinline register: RegisterProvider<T>) =
        addRegister(T::class.java, register)

    /**
     * Adds all registers for all forge registries defined in [ForgeRegistries]
     */
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

    /**
     * Builds a [RegisterHelper] for a given mod, specified by [modId].
     */
    fun build(modId: String): RegisterHelper {
        return RegisterHelper(
            modId,
            registerFactory,
            registerProviders
        )
    }
}

/**
 * Builder function for creating a [RegisterHelper].
 *
 * Example Usage:
 * ```
 * registerHelper(MyMod.Id) {
 *   addForgeRegisters()
 *   addRegister { modId ->
 *     DeferredRegister.create(myRegistry, modId)
 *   }
 * }
 * ```
 */
fun registerHelper(modId: String, init: RegisterHelperBuilder.() -> Unit): RegisterHelper {
    return RegisterHelperBuilder()
        .apply(init)
        .build(modId)
}
