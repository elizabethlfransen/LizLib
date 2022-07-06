package io.github.elizabethlfransen.lizlib.register

import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister

/**
 * Used to quickly get registers on demand.
 * A RegisterHelper is tied to a specific mod, specified by [modId] and a supplied map for getting a register by type.
 */
class RegisterHelper(private val modId: String, private val registerFactory: IDeferredRegisterFactory, private val registerProviders: Map<Class<*>, RegisterProvider<*>>) {
    private val registers = mutableMapOf<Class<*>, IDeferredRegister<*>>()
    private val eventBuses = mutableSetOf<IEventBus>()

    /**
     * Adds an event bus to add registers to.
     * Existing registers will be registered.
     * Future registers will be added.
     */
    fun register(eventBus: IEventBus) {
        // register existing registers
        registers.values.forEach { register -> register.register(eventBus) }
        // remember for future registers
        eventBuses.add(eventBus)
    }

    /**
     * Resolves a register for [registerType]. If the register has already used it's not recreated. If there is no
     * provider for [registerType], [IllegalArgumentException] is thrown.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> resolveRegister(registerType: Class<T>): IDeferredRegister<T> {
        // if register already exists return it
        if(registers.containsKey(registerType))
            return registers[registerType] as IDeferredRegister<T>
        // if no register provider exists for type throw an error
        if(!registerProviders.containsKey(registerType))
            throw IllegalArgumentException("No register provider for type \"${registerType.simpleName}\"")
        // register must be created
        val register = registerProviders[registerType]!!(modId, registerFactory)
        // register to existing event busses
        eventBuses.forEach(register::register)
        // remember for later
        registers[registerType] = register
        return register as IDeferredRegister<T>
    }
    /**
     * Resolves a register of type [T]. If the register has already used it's not recreated. If there is no
     * provider of type [T], [IllegalArgumentException] is thrown.
     */
    inline fun <reified T> resolveRegister()
        = resolveRegister(T::class.java)
}