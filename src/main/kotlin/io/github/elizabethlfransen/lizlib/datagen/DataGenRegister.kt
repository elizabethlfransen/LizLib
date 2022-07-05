package io.github.elizabethlfransen.lizlib.datagen

import io.github.elizabethlfransen.lizlib.LizLib
import io.github.elizabethlfransen.lizlib.util.runLogging
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation.EnumHolder
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData
import net.minecraftforge.forgespi.locating.IModFile
import org.objectweb.asm.Type
import org.slf4j.LoggerFactory
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.lang.reflect.Parameter

// Used to inject parameters
private val providerInjectionTypes = mapOf<Class<*>, (GatherDataEvent) -> Any>(
    String::class.java to { it.modContainer.modId },
    ModContainer::class.java to { it.modContainer },
    DataGenerator::class.java to { it.generator },
    ExistingFileHelper::class.java to { it.existingFileHelper },
    GatherDataEvent::class.java to { it }
)

// logger to use while registering
private val logger = LoggerFactory.getLogger(LizLib.ID)

/**
 * Registers all data generators for a given mod, specified by [modId], for a given [eventBus]. The event bus is
 * usually [MOD_BUS]
 */
fun registerDataGenerators(modId: String, eventBus: IEventBus) {
    val modFile = ModList.get().getModFileById(modId)?.file
    logger.debug("Begin scanning for @DataGen in $modId")
    logger.runLogging<DataGenRegistrationException>("Unable to register generators for $modId") {
        if (modFile == null) {
            throw DataGenRegistrationException("Unable to get get mod file for $modId")
        }
        registerDataGenerators(modFile, eventBus)
    }
    logger.debug("Finish scanning for @DataGen in $modId")
}

private fun registerDataGenerators(
    modFile: IModFile,
    eventBus: IEventBus
) {
    modFile.scanResult
        .annotations
        .filter { it.annotationType == Type.getType(DataGen::class.java) }
        .forEach {
            logger.runLogging<DataGenRegistrationException>("Unable to to register Data Provider \"${it.clazz.className}\"") {
                registerDataGenerator(it, eventBus)
            }
        }
}


private fun createProviderProvider(type: Type): (event: GatherDataEvent) -> DataProvider {
    logger.debug("Scanning for valid constructor on ${type.className}")
    val javaClass = try {
        Class.forName(type.className)
    } catch (e: ClassNotFoundException) {
        throw DataGenRegistrationException("Unable to get Provider Class", e)
    }

    val constructor = javaClass.constructors.singleOrNull()
        ?: throw DataGenRegistrationException("No valid constructors for ${type.className}")

    val parameters = constructor.parameters
        .map(Parameter::getType)
        .map { parameterType ->
            providerInjectionTypes[parameterType]
                ?: throw DataGenRegistrationException("Invalid parameter type for ${type.className}: ${parameterType.simpleName}")
        }


    return { event ->
        val realizedParameters = parameters
            .map { it(event) }
        try {
            constructor.newInstance(*realizedParameters.toTypedArray()) as DataProvider
        } catch (e: Throwable) {
            throw DataGenRegistrationException("Unable to initialize ${type.className}", e)
        }
    }

}

@Suppress("UNCHECKED_CAST")
private fun registerDataGenerator(annotationData: AnnotationData, eventBus: IEventBus) {
    logger.debug("Auto-registering ${annotationData.clazz.className}")
    val providerProvider = createProviderProvider(annotationData.clazz)
    val includedGeneratorTypes = (annotationData.annotationData["includeIn"] as? List<EnumHolder> ?: emptyList())
        .map { it.value }
        .map { DataGen.DataGeneratorType.valueOf(it) }
    eventBus.addListener<GatherDataEvent> { event ->
        logger.runLogging<DataGenRegistrationException>("Unable to create register provider") {
            // only add if any of the generator types are included
            if (includedGeneratorTypes.any { it.isIncluded(event) }) {
                val provider = providerProvider(event)
                logger.debug("Adding provider \"{}\"", provider::class.java)
                event.generator.addProvider(provider)
            }
        }
    }
}