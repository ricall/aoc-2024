package org.ricall.day17

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private val REGISTER_REGEX = "^Register ([ABC]): ([0-9]+)$".toRegex()
private val PROGRAM_REGEX = "^Program: (.*)$".toRegex()

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
            INSTRUCTIONS[program[instructionPointer]]()
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

        var candidate = nextValue * 8
        repeat(8) {
            registerA = candidate
            execute()
            if (output.first() == program[index]) {
                val finalVal = findRegisterAThatOutputsProgram(candidate, index - 1)
                if (finalVal >= 0) {
                    return finalVal
                }
            }
            candidate++
        }
        return -1
    }

    private val INSTRUCTIONS = listOf<() -> Unit>(
        { repeat(combo()) { registerA /= 2 } },                             // ADV
        { registerB = registerB xor literal().toLong() },                   // BXL
        { registerB = (combo() and 7).toLong() },                           // BST
        { if (registerA != 0L) { instructionPointer = literal() - 2 } },    // JNZ
        { registerB = registerB xor registerC },                            // BXC
        { output.add(combo() and 7) },                                      // OUT
        { registerB = registerA; repeat(combo()) { registerB /= 2 } },      // BDV
        { registerC = registerA; repeat(combo()) { registerC /= 2 } },      // CDV
    )

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
        val machine = Machine(File("./inputs/day17.txt").readText()).execute()

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