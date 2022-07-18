package io.github.elizabethlfransen.lizlib.register.item

import net.minecraft.world.item.Item

class ItemRegistrationConfig(
) {
    var props: ItemPropsConfig = ItemPropsConfig()

    fun props(init: ItemPropsConfig.() -> Unit) {
        props.init()
    }

    fun build() = Item(props.build())
}