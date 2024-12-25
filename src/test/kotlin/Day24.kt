package org.ricall.day24

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private enum class Operation { AND, OR, XOR }

private data class Gate(val in1: String, val in2: String, val op: Operation, var out: String) {
    fun hasInput(input: String) = in1 == input || in2 == input
    fun dependsOn(gate: Gate) = (gate.out == in1  || gate.out == in2)
}

private typealias Gates = MutableList<Gate>
private typealias Registers = MutableMap<String, Int>
private fun parse(input: String): Pair<Registers, Gates> {
    val (wireInput, gateInput) = input.split("\n\n")
    val gates = gateInput.lines()
        .map { it.split(" ").let {
            Gate(it[0], it[2], Operation.valueOf(it[1]), it[4]) }
        }.toMutableList()
    val registers = wireInput.lines()
        .associate { it.split(": ").let { it.first() to it.last().toInt() } }
    return registers.toMutableMap() to gates
}

private fun solvePart1(input: String) = parse(input).let { (registers, gates) -> execute(registers, gates) }

/**
 * x01 XOR y01 -> firstResult (g1)
 * x01 AND y01 -> firstCarry (g2)
 * firstResult XOR previousFinalCarry -> z01 (g3)
 * firstResult AND previousFinalCarry -> secondCarry (g4)
 * firstCarry OR secondCarry -> finalCarry (g5)
 */
private fun solvePart2(input: String): String {
    val (registers, gates) = parse(input)

    val invalidZ = gates.filter { it.out.first() == 'z' && it.out != "z45" && it.op != Operation.XOR } // z should be on g3 only
    val invalidXOR = gates.filter { it.in1.first() !in "xy" && it.in2.first() !in "xy" && it.out.first() != 'z' && it.op == Operation.XOR } // should be a g3

    for (gate in invalidXOR) {
        // Fix up g3 gates so we can calculate carry properly
        val b = invalidZ.first { it.out == gates.findZOutputThatUses(gate.out) }
        val tempOutput = gate.out
        gate.out = b.out
        b.out = tempOutput
    }

    val invalidBit = (getOutput(registers, 'x') + getOutput(registers, 'y') xor execute(registers, gates)).countTrailingZeroBits().toString()
    val invalidCarry = gates.filter { it.in1.endsWith(invalidBit) && it.in2.endsWith(invalidBit) }
    return (invalidZ + invalidXOR + invalidCarry).map { it.out }.sorted().joinToString(",")
}

private fun List<Gate>.findZOutputThatUses(input: String): String? {
    val candidateGates = filter { it.hasInput(input) }
    candidateGates.find { it.out.first() == 'z' }?.let {
        return "z" + (it.out.drop(1).toInt() - 1).toString().padStart(2, '0')
    }
    return candidateGates.firstNotNullOfOrNull { findZOutputThatUses(it.out) }
}

private fun execute(registers: Registers, gates: Gates): Long {
    val processedGates = HashSet<Gate>()
    while (processedGates.size < gates.size) {
        val unprocessedGates = gates.filter { it !in processedGates }
        val available = unprocessedGates.filter { gate -> unprocessedGates.none { gate.dependsOn(it) } }

        for ((in1, in2, op, out) in available) {
            val v1 = registers.getOrDefault(in1, 0)
            val v2 = registers.getOrDefault(in2, 0)
            registers[out] = when (op) {
                Operation.AND -> v1 and v2
                Operation.OR -> v1 or v2
                Operation.XOR -> v1 xor v2
            }
        }
        processedGates.addAll(available)
    }
    return getOutput(registers, 'z')
}

private fun getOutput(registers: Registers, type: Char) = registers
    .entries
    .filter { it.key.startsWith(type) }
    .sortedBy { it.key }
    .map { it.value }
    .joinToString("")
    .reversed()
    .toLong(2)

class Day24 {
    @Test
    fun `part 1 test data`() {
        val result = solvePart1(
            """
                |x00: 1
                |x01: 1
                |x02: 1
                |y00: 0
                |y01: 1
                |y02: 0
                |
                |x00 AND y00 -> z00
                |x01 XOR y01 -> z01
                |x02 OR y02 -> z02
            """.trimMargin()
        )

        assertEquals(4, result)
    }

    @Test
    fun `part 1 test data2`() {
        val result = solvePart1(
            """
                |x00: 1
                |x01: 0
                |x02: 1
                |x03: 1
                |x04: 0
                |y00: 1
                |y01: 1
                |y02: 1
                |y03: 1
                |y04: 1
                |
                |ntg XOR fgs -> mjb
                |y02 OR x01 -> tnw
                |kwq OR kpj -> z05
                |x00 OR x03 -> fst
                |tgd XOR rvg -> z01
                |vdt OR tnw -> bfw
                |bfw AND frj -> z10
                |ffh OR nrd -> bqk
                |y00 AND y03 -> djm
                |y03 OR y00 -> psh
                |bqk OR frj -> z08
                |tnw OR fst -> frj
                |gnj AND tgd -> z11
                |bfw XOR mjb -> z00
                |x03 OR x00 -> vdt
                |gnj AND wpb -> z02
                |x04 AND y00 -> kjc
                |djm OR pbm -> qhw
                |nrd AND vdt -> hwm
                |kjc AND fst -> rvg
                |y04 OR y02 -> fgs
                |y01 AND x02 -> pbm
                |ntg OR kjc -> kwq
                |psh XOR fgs -> tgd
                |qhw XOR tgd -> z09
                |pbm OR djm -> kpj
                |x03 XOR y03 -> ffh
                |x00 XOR y04 -> ntg
                |bfw OR bqk -> z06
                |nrd XOR fgs -> wpb
                |frj XOR qhw -> z04
                |bqk OR frj -> z07
                |y03 OR x01 -> nrd
                |hwm AND bqk -> z03
                |tgd XOR rvg -> z12
                |tnw OR pbm -> gnj
            """.trimMargin()
        )

        assertEquals(2024, result)
    }

    @Test
    fun `part 1`() {
        val result = solvePart1(File("./inputs/day24.txt").readText())

        assertEquals(58740594706150, result)
    }

    @Test
    fun `part 2`() {
        val result = solvePart2(File("./inputs/day24.txt").readText())

        assertEquals("cvh,dbb,hbk,kvn,tfn,z14,z18,z23", result)
    }
}