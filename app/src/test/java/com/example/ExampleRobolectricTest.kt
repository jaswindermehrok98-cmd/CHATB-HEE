package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.content.DropsEngine
import com.example.content.TheReadEngine
import com.example.message.TodayMessageEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("UNPLUG LABS® OFFLINE OS", appName)
    }

    @Test
    fun `today message engine produces valid context message`() {
        val message = TodayMessageEngine.getMessageForCurrentContext(isFocusActive = false)
        assertNotNull(message)
        assertTrue(message.text.isNotBlank())
        assertTrue(message.category.isNotBlank())
    }

    @Test
    fun `the read calculates sovereign operator archetype`() {
        val highScores = listOf(3, 3, 3, 3, 3)
        val result = TheReadEngine.calculateResult(highScores)
        assertEquals("THE SOVEREIGN OPERATOR", result.archetype)
        assertEquals(15, result.score)
    }

    @Test
    fun `drops engine provides valid interactive artifacts`() {
        val drop1 = DropsEngine.getDrop("DROP 001")
        assertNotNull(drop1)
        assertEquals("THE WAIT", drop1?.title)
    }
}
