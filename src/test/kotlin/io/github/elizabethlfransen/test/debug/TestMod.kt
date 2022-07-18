package io.github.elizabethlfransen.test.debug

import io.github.elizabethlfransen.lizlib.datagen.DataGen
import io.github.elizabethlfransen.lizlib.datagen.DataGen.DataGeneratorType.CLIENT
import io.github.elizabethlfransen.lizlib.datagen.registerDataGenerators
import io.github.elizabethlfransen.lizlib.register.item.item
import io.github.elizabethlfransen.lizlib.register.registerHelper
import io.github.elizabethlfransen.test.debug.TestMod.registers
import net.minecraft.data.DataGenerator
import net.minecraftforge.common.data.LanguageProvider
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.forge.MOD_BUS

object TestItems {
    val tinIngot = registers.item("test") {
        props {
            stacksTo = 16
        }
    }
}

@Mod(TestMod.ID)
object TestMod {
    const val ID: String = "liz_lib_test_mod"
    val logger = LogManager.getLogger(ID)

    val registers = registerHelper(ID) {
        addForgeRegisters()
    }

    init {
        registerDataGenerators(ID, MOD_BUS)
    }
}

@DataGen(
    includeIn = [
        CLIENT
    ]
)
class ExampleDataGen(generator: DataGenerator, modId: String) : LanguageProvider(generator, modId, "en_us") {
    override fun addTranslations() {
        add("test", "test")
    }
}