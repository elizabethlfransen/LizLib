package io.github.elizabethlfransen.test.register

import io.github.elizabethlfransen.lizlib.register.IDeferredRegister
import io.github.elizabethlfransen.lizlib.register.IDeferredRegisterFactory
import io.github.elizabethlfransen.lizlib.register.RegisterHelper
import io.github.elizabethlfransen.lizlib.register.RegisterProvider
import io.github.elizabethlfransen.test.debug.TestMod
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.eventbus.api.IEventBus
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class MockRegisterMap(
    vararg registerTypesToMock: Class<*>
) {
    val registers = registerTypesToMock.associateWith { _ ->
        mock<IDeferredRegister<*>>()
    }
    val registerMap: Map<Class<*>, RegisterProvider<*>> = registers
        .mapValues { v ->
            mock {
                on {
                    invoke(any(), any())
                }.thenReturn(v.value)
            }
        }
}

class RegisterHelperTests {
    @Nested
    inner class RegisterTests {
        @Test
        fun `resolving a register without any event busses does not register the deferred register to any event bus`() {
            // arrange
            val register = mock<IDeferredRegister<Item>>()
            val registerFactory = mock<IDeferredRegisterFactory>()
            val registerMap = mapOf<Class<*>, RegisterProvider<*>>(
                Item::class.java to { _, _ -> register }
            )
            val handler = RegisterHelper(
                "TestId",
                registerFactory,
                registerMap
            )
            // act
            handler.resolveRegister<Item>()

            // assert
            verify(register, never()).register(any())
        }

        @Test
        fun `adding an event bus without resolving any registers does not register the event bus to anything`() {
            // arrange
            val registerFactory = mock<IDeferredRegisterFactory>()
            val eventBus = mock<IEventBus>()
            val handler = RegisterHelper(
                "TestId",
                registerFactory,
                emptyMap()
            )
            // act
            handler.register(eventBus)
            // assert
            // succeeds if no errors are thrown
        }

        @Test
        fun `resolving a register with a single event bus registers to the given event bus`() {
            // arrange
            val register = mock<IDeferredRegister<Item>>()
            val registerFactory = mock<IDeferredRegisterFactory>()
            val eventBus = mock<IEventBus>()
            val registerMap = mapOf<Class<*>, RegisterProvider<*>>(
                Item::class.java to { _, _ -> register }
            )
            val handler = RegisterHelper(
                "TestId",
                registerFactory,
                registerMap
            )
            // act
            handler.register(eventBus)
            handler.resolveRegister<Item>()

            // assert
            verify(register).register(eventBus)

        }

        @Test
        fun `resolving a register with multiple event busses registers to each event bus`() {
            // arrange
            val register = mock<IDeferredRegister<Item>>()
            val registerFactory = mock<IDeferredRegisterFactory>()
            val eventBusses = listOf(mock<IEventBus>(), mock())
            val registerMap = mapOf<Class<*>, RegisterProvider<*>>(
                Item::class.java to { _, _ -> register }
            )
            val handler = RegisterHelper(
                "TestId",
                registerFactory,
                registerMap
            )
            // act
            eventBusses.forEach(handler::register)
            handler.resolveRegister<Item>()

            // assert
            eventBusses.forEach {
                verify(register).register(it)
            }
        }

        @Test
        fun `registering an event bus after resolving a register registers the register to the event bus`() {
            // arrange
            val register = mock<IDeferredRegister<Item>>()
            val registerFactory = mock<IDeferredRegisterFactory>()
            val eventBus = mock<IEventBus>()
            val registerMap = mapOf<Class<*>, RegisterProvider<*>>(
                Item::class.java to { _, _ -> register }
            )
            val handler = RegisterHelper(
                "TestId",
                registerFactory,
                registerMap
            )
            // act
            handler.resolveRegister<Item>()
            handler.register(eventBus)

            // assert
            verify(register).register(eventBus)
        }

        @Test
        fun `registering an event bus after resolving multiple registers registers all registers to the event bus`() {
            val registers = arrayOf(mock<IDeferredRegister<Item>>(), mock<IDeferredRegister<Block>>())
            val registerFactory = mock<IDeferredRegisterFactory>()
            val eventBus = mock<IEventBus>()
            val registerMap = mapOf<Class<*>, RegisterProvider<*>>(
                Item::class.java to { _, _ -> registers[0] },
                Block::class.java to {_, _ -> registers[1] }
            )
            val handler = RegisterHelper(
                "TestId",
                registerFactory,
                registerMap
            )
            // act
            handler.resolveRegister<Item>()
            handler.resolveRegister<Block>()
            handler.register(eventBus)

            // assert
            registers.forEach {register ->
                verify(register).register(eventBus)
            }
        }

    }

    @Nested
    inner class ResolveRegisterTests {
        private val registers = MockRegisterMap(
            Item::class.java,
            Block::class.java
        )
        private val registerFactory = mock<IDeferredRegisterFactory>()
        private val modId = TestMod.ID
        private val helper = RegisterHelper(
            modId,
            registerFactory,
            registers.registerMap
        )

        @Test
        fun `resolving a new register initializes a new register`() {
            val result = helper.resolveRegister<Block>()
            assertThat(result).isSameAs(registers.registers[Block::class.java])
            verify(registers.registerMap[Block::class.java])!!.invoke(modId, registerFactory)
            verify(registers.registerMap[Item::class.java], never())!!.invoke(any(), any())
        }

        @Test
        fun `resolving an existing register does not initializes a new register and returns previous register`() {
            val result = helper.resolveRegister<Block>()
            assertThat(helper.resolveRegister<Block>()).isSameAs(result)
            verify(registers.registerMap[Block::class.java])!!.invoke(modId, registerFactory)
        }

        @Test
        fun `resolving an invalid register throws an exception`() {
            assertThatThrownBy{
                helper.resolveRegister<Fluid>()
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("No register provider for type \"Fluid\"")
        }
    }
}