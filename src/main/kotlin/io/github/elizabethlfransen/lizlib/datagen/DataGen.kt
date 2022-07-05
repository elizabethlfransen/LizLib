package io.github.elizabethlfransen.lizlib.datagen

import net.minecraftforge.forge.event.lifecycle.GatherDataEvent

/**
 * Registers a data provider to be automatically registered
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DataGen(
    val includeIn: Array<DataGeneratorType> = []
) {
    enum class DataGeneratorType(
        val isIncluded: (event: GatherDataEvent) -> Boolean
    ) {
        DEV(GatherDataEvent::includeDev),
        CLIENT(GatherDataEvent::includeClient),
        SERVER(GatherDataEvent::includeDev),
        REPORTS(GatherDataEvent::includeReports)
    }
}
