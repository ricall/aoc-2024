package org.ricall.day9

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private val TEST_DATA = "2333133121414131402"

private val EMPTY = -1L

private data class V2File(var fileId: Long, var start: Int, var length: Int)

private fun uncompress(diskMap: String) = buildList {
    var fileNumber = 0L
    var isEmpty = false
    var index = 0

    for (ch in diskMap) {
        val id = when (isEmpty) {
            true -> EMPTY
            false -> fileNumber++
        }
        isEmpty = !isEmpty
        val length = "${ch}".toInt()
        repeat(length) { add(id) }
        index += length
    }
}

private fun compact(disk: List<Long>) = buildList<Long> {
    var start = 0
    var end = disk.size - 1
    while (start <= end) {
        when (disk[start]) {
            EMPTY -> {
                while (disk[end] == EMPTY) {
                    end--
                }
                add(disk[end--])
                start++
            }
            else -> add(disk[start++])
        }
    }
}

private fun calculateDiskChecksum(diskMap: String): Long {
    val disk = uncompress(diskMap)
    val compacted = compact(disk)

    return compacted.foldIndexed(0L) { index, total, id -> total + index * id }
}

private fun uncompressV2(diskMap: String)= buildList<V2File> {
    var fileNumber = 0L
    var isEmpty = false
    var index = 0

    for (ch in diskMap) {
        val id = when (isEmpty) {
            true -> EMPTY
            false -> fileNumber++
        }
        isEmpty = !isEmpty
        val length = "${ch}".toInt()

        add(V2File(fileId = id, start = index, length = length))
        index += length
    }
}

private fun findEmptyBlock(files: List<V2File>, length: Int): Int {
    files.forEachIndexed { index, file ->
        if (file.fileId == EMPTY && file.length >= length) {
            return index
        }
    }
    return -1
}

private data class Candidate(val file: V2File, val index: Int)
private fun getCandidateFile(files: List<V2File>): Candidate? {
    (files.size - 1 downTo 0).forEach { idx ->
        val file = files[idx]
        if (file.fileId >= 0) {
            val index = findEmptyBlock(files, file.length)
            if (index >= 0 && files[index].start < file.start) {
                return Candidate(file, index)
            }
        }
    }
    return null
}

private fun convert(files: List<V2File>): List<Long> = buildList<Long> {
    files.forEach { file -> repeat(file.length) { add(file.fileId) } }
}

private fun compactV2(files: List<V2File>): List<Long> {
    val newFiles = files.toMutableList()
    var candidate = getCandidateFile(newFiles)
    while (candidate != null) {
        val (file, index) = candidate

        val empty = newFiles[index]

        newFiles.add(index, V2File(fileId = file.fileId, start = empty.start, length = file.length))

        // Mark old file as empty and adjust the size of the empty block (possibly to 0)
        file.fileId = EMPTY
        empty.length-= file.length
        empty.start += file.length
        candidate = getCandidateFile(newFiles)
    }
    return convert(newFiles)
}

fun calculateDiskChecksumWithCompactV2(diskMap: String): Long {
    val files = uncompressV2(diskMap)
    val compacted = compactV2(files)

    return compacted.foldIndexed(0L) { index, total, id -> total + index * if (id > 0) id else 0 }
}


class Day09 {
    @Test
    fun `part 1 test data`() {
        val result = calculateDiskChecksum(TEST_DATA)
        assertEquals(1928, result)
    }

    @Test
    fun `part 1`() {
        val result = calculateDiskChecksum(File("./inputs/day9.txt").readText())
        assertEquals(6258319840548, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = calculateDiskChecksumWithCompactV2(TEST_DATA)
        assertEquals(2858, result)
    }

    @Test
    fun `part 2`() {
        val result = calculateDiskChecksumWithCompactV2(File("./inputs/day9.txt").readText())
        assertEquals(6286182965311, result)
    }
}

