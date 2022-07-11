package io.github.elizabethlfransen.lizlib.register.item

import io.github.elizabethlfransen.lizlib.register.IDeferredRegister
import io.github.elizabethlfransen.lizlib.register.RegisterHelper
import net.minecraft.world.item.Item
import kotlin.properties.ReadOnlyProperty

fun IDeferredRegister<Item>.item(name: String, init: ItemRegistrationConfig.() -> Unit = {}): ReadOnlyProperty<Any?, Item> {
    val config = ItemRegistrationConfig()
    config.init()
    return registerObject(name, config::build)
}

fun RegisterHelper.item(name: String, init: ItemRegistrationConfig.() -> Unit = {}): ReadOnlyProperty<Any?, Item> {
    return resolveRegister<Item>()
        .item(name, init)
}