package org.ricall.day17

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private val REGISTER_REGEX = "^Register ([ABC]): ([0-9]+)$".toRegex()
private val PROGRAM_REGEX = "^Program: (.*)$".toRegex()

private fun interface OpcodeExecutor { fun execute(machine: Machine) }
private enum class Command(val executor: OpcodeExecutor) : OpcodeExecutor {
    ADV({ machine -> repeat(machine.combo()) { machine.registerA /= 2 } }),
    BXL({ it.registerB = it.registerB xor it.literal().toLong() }),
    BST({ it.registerB = (it.combo() and 7).toLong() }),
    JNZ({ if (it.registerA != 0L) { it.instructionPointer = it.literal() - 2 } }),
    BXC({ it.registerB = it.registerB xor it.registerC }),
    OUT({ it.output.add(it.combo() and 7) }),
    BDV({ machine ->
        machine.registerB = machine.registerA
        repeat(machine.combo()) { machine.registerB /= 2 }
    }),
    CDV({ machine ->
        machine.registerC = machine.registerA
        repeat(machine.combo()) { machine.registerC /= 2 }
    });

    override fun execute(machine: Machine) = this.executor.execute(machine)

    companion object {
        fun fromOpcode(opcode: Int) = entries[opcode]
    }
}

private class Machine(input: String) {
    var registerA: Long = 0
    var registerB: Long = 0
    var registerC: Long = 0
    var instructionPointer: Int = 0

    val program: List<Int>
    val output = mutableListOf<Int>()

    fun execute(): Machine {
        output.clear()
        instructionPointer = 0
        while (instructionPointer < program.size) {
            Command.fromOpcode(program[instructionPointer]).execute(this)
            instructionPointer += 2
        }
        return this
    }

    fun literal(): Int = program[instructionPointer + 1]

    fun combo(): Int = when (val literal = literal()) {
        0, 1, 2, 3 -> literal
        4 -> registerA.toInt()
        5 -> registerB.toInt()
        6 -> registerC.toInt()
        else -> throw Exception("Unsupported combo")
    }

    fun findRegisterAThatOutputsProgram(nextValue: Long = 0, index: Int = program.size - 1): Long {
        if (index < 0) {
            return nextValue
        }

        var a = nextValue * 8
        while (a < nextValue * 8 + 8) {
            registerA = a
            execute()
            if (output.first() == program[index]) {
                val finalVal = findRegisterAThatOutputsProgram(a, index - 1)
                if (finalVal >= 0) return finalVal
            }
            a++
        }
        return -1
    }

    init {
        var parsedProgram: List<Int>? = null
        input.lines().forEach { line ->
            REGISTER_REGEX.find(line)?.let { match ->
                val (register, value) = match.destructured
                when (register) {
                    "A" -> registerA = value.toLong()
                    "B" -> registerB = value.toLong()
                    "C" -> registerC = value.toLong()
                }
            }
            PROGRAM_REGEX.find(line)?.let { match ->
                val (programText) = match.destructured
                parsedProgram = programText.split(",").map(String::toInt)
            }
        }
        this.program = parsedProgram ?: throw IllegalArgumentException("Invalid input")
    }
}

class Day17 {
    @Test
    fun `part 1 - test1`() {
        val machine = Machine(
            """
            |Register C: 9
            |Program: 2,6""".trimMargin()
        ).execute()

        assertEquals(1, machine.registerB)
    }

    @Test
    fun `part 1 - test2`() {
        val machine = Machine(
            """
            |Register A: 10
            |Program: 5,0,5,1,5,4""".trimMargin()
        ).execute()

        assertEquals(listOf(0, 1, 2), machine.output)
    }

    @Test
    fun `part 1 - test3`() {
        val machine = Machine(
            """
            |Register A: 2024
            |Program: 0,1,5,4,3,0""".trimMargin()
        ).execute()

        assertEquals(listOf(4, 2, 5, 6, 7, 7, 7, 7, 3, 1, 0), machine.output)
        assertEquals(0, machine.registerA)
    }

    @Test
    fun `part 1 - test4`() {
        val machine = Machine(
            """
            |Register B: 29
            |Program: 1,7""".trimMargin()
        ).execute()

        assertEquals(26, machine.registerB)
    }

    @Test
    fun `part 1 - test5`() {
        val machine = Machine(
            """
            |Register B: 2024
            |Register C: 43690
            |Program: 4,0""".trimMargin()
        ).execute()

        assertEquals(44354, machine.registerB)
    }

    @Test
    fun `part 1 - test data`() {
        val machine = Machine("""
            |Register A: 729
            |Register B: 0
            |Register C: 0
            |
            |Program: 0,1,5,4,3,0""".trimMargin()
        ).execute()

        assertEquals(listOf(4, 6, 3, 5, 6, 3, 5, 2, 1, 0), machine.output)
    }

    @Test
    fun `part 1`() {
        val machine = Machine(File("./inputs/day17.txt").readText())
        machine.execute()

        assertEquals("7,4,2,5,1,4,6,0,4", machine.output.joinToString(","))
    }

    @Test
    fun `part 2 - test data`() {
        val machine = Machine("""
            |Register A: 2024
            |Register B: 0
            |Register C: 0
            |
            |Program: 0,3,5,4,3,0""".trimMargin())
        val registerA = machine.findRegisterAThatOutputsProgram()

        assertEquals(117440, registerA)
        machine.registerA = registerA
        machine.execute()
        assertEquals(machine.program, machine.output)
    }

    @Test
    fun `part 2`() {
        val machine = Machine(File("./inputs/day17.txt").readText())
        val registerA = machine.findRegisterAThatOutputsProgram()

        assertEquals(164278764924605, registerA)
        machine.registerA = registerA
        machine.execute()
        assertEquals(machine.program, machine.output)
    }
}