package io.github.elizabethlfransen.lizlib.register.item

import net.minecraft.world.item.Item

class ItemRegistrationConfig(
) {
    var props: ItemPropsConfig = ItemPropsConfig()
    fun build() = Item(props.build())
}