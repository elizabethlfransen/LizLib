package io.github.elizabethlfransen.lizlib

import net.minecraftforge.fml.common.Mod
import org.slf4j.LoggerFactory

/**
 * Main mod class. Should be an `object` declaration annotated with `@Mod`.
 * The modid should be declared in this object and should match the modId entry
 * in mods.toml.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */
@Mod(LizLib.ID)
object LizLib {
    const val ID = "liz_lib"
    val LOGGER = LoggerFactory.getLogger(ID)
}
