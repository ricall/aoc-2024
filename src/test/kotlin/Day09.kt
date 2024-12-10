package org.ricall.day9

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private const val TEST_DATA = "2333133121414131402"
private const val FREE = Integer.MIN_VALUE

private class Disk(diskMap: String) {
    val blocks = parseInput(diskMap).toMutableList()

    fun compact(): Disk {
        var start = 0
        var end = blocks.size - 1
        while (start < end) {
            when (blocks[start]) {
                FREE -> {
                    while (blocks[end] == FREE) {
                        end--
                    }
                    blocks[start++] = blocks[end]
                    blocks[end--] = FREE
                }
                else -> start++
            }
        }
        return this
    }

    fun compactV2(): Disk {
        var end = blocks.size - 1
        while (end > 0) {
            while (blocks[end] == FREE) {
                end--
            }
            val start = blocks.indexOf(blocks[end])
            val length = end - start + 1

            val freeStart = findFreeBlock(length)
            if (freeStart != null && freeStart < end) {
                (0..<length).forEach {
                    blocks[freeStart + it] = blocks[start + it]
                    blocks[start + it] = FREE
                }
            }
            end -= length
        }
        return this
    }

    fun checksum() = blocks.foldIndexed(0L) { index, checksum, id ->
        when {
            id > 0 -> checksum + index * id
            else -> checksum
        }
    }

    private fun parseInput(diskMap: String) = buildList {
        var fileNumber = 0
        diskMap.forEachIndexed { index, ch ->
            val fileId = when (index % 2) {
                0 -> fileNumber++
                else -> FREE
            }
            repeat(ch.digitToInt()) { add(fileId) }
        }
    }

    private fun findFreeBlock(length: Int): Int? {
        var start = 0
        while (start < blocks.size - length) {
            if ((0..<length).all { blocks[start + it] == FREE }) {
                return start
            }
            start++
        }
        return null
    }

    override fun toString() = blocks.map { if (it < 0) "." else "${it}" }.joinToString("")
}

class Day09 {
    @Test
    fun `part 1 test data`() {
        val result = Disk(TEST_DATA).compact().checksum()
        assertEquals(1928, result)
    }

    @Test
    fun `part 1`() {
        val result = Disk(File("./inputs/day9.txt").readText()).compact().checksum()
        assertEquals(6258319840548, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = Disk(TEST_DATA).compactV2().checksum()
        assertEquals(2858, result)
    }

    @Test
    fun `part 2`() {
        val result = Disk(File("./inputs/day9.txt").readText()).compactV2().checksum()
        assertEquals(6286182965311, result)
    }
}

