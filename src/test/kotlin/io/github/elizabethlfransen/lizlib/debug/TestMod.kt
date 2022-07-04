package io.github.elizabethlfransen.lizlib.debug

import io.github.elizabethlfransen.lizlib.register.registerHelper
import net.minecraftforge.fml.common.Mod

@Mod(TestMod.ID)
object TestMod {
    const val ID: String = "liz_lib_test_mod"

    val registers = registerHelper(ID) {
        addForgeRegisters()
    }
}