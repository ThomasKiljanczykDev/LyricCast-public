/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 1:03 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 1:02 PM
 */

package dev.thomas_kiljanczyk.lyriccast.common.tests

import com.google.common.truth.Truth.assertThat
import dev.thomas_kiljanczyk.lyriccast.common.helpers.FileHelper
import java.io.File
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class FileHelperTest {

    private companion object {
        const val TEST_FILE_CONTENT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit"
    }

    private lateinit var testDir: File
    private lateinit var testContentDir: File
    private lateinit var testFile: File
    private lateinit var testZipFile: File

    @Before
    fun setup() {
        testDir = File("FileHelperTest")
        testDir.deleteRecursively()
        testDir.mkdirs()

        // The zip file is kept outside of the zipped directory so that it is not archived into
        // itself.
        testContentDir = File("${testDir.path}/content")
        testContentDir.mkdirs()

        testFile = File("${testContentDir.path}/FileHelperTest.txt")
        testZipFile = File("${testDir.path}/FileHelperTest.zip")

        testFile.createNewFile()
        testFile.writeText(TEST_FILE_CONTENT)
    }

    @After
    fun cleanUp() {
        try {
            testDir.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun fileIsZippedAndUnzipped() {
        val zipSuccessful = FileHelper.zip(testZipFile.outputStream(), testFile.parent!!)

        assertThat(zipSuccessful).isTrue()
        assertThat(testZipFile.exists()).isTrue()
        assertThat(testZipFile.readBytes().size).isGreaterThan(0)
        testFile.delete()

        try {
            FileHelper.unzip(testZipFile.inputStream(), testFile.parent!!)
        } catch (e: Exception) {
            e.printStackTrace()
            fail("FileHelper.unzip failed.")
        }

        assertThat(testFile.exists()).isTrue()
        assertThat(testFile.readText()).isEqualTo(TEST_FILE_CONTENT)
    }
}
