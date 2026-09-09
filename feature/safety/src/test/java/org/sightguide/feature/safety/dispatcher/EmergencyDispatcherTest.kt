package org.sightguide.feature.safety.dispatcher

import android.content.Context
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.sightguide.core.common.model.Coordinates
import org.sightguide.core.storage.dao.AuditTrailDao

class EmergencyDispatcherTest {

    private val context: Context = mockk(relaxed = true)
    private val auditTrailDao: AuditTrailDao = mockk(relaxed = true)
    private lateinit var dispatcher: EmergencyDispatcher

    @Before
    fun setUp() {
        coEvery { auditTrailDao.insertLog(any()) } returns 1L
        dispatcher = EmergencyDispatcher(context, auditTrailDao)
    }

    @Test
    fun buildSosMessage_withCoordinates_includesGoogleMapsLink() {
        val coords = Coordinates(37.7749, -122.4194)
        val message = dispatcher.buildSosMessage(coords)

        assertNotNull(message)
        assertTrue(message.contains("SIGHTGUIDE SOS Alert"))
        assertTrue(message.contains("https://maps.google.com/?q=37.7749,-122.4194"))
    }

    @Test
    fun buildSosMessage_nullCoordinates_includesFallback() {
        val message = dispatcher.buildSosMessage(null)

        assertTrue(message.contains("SIGHTGUIDE SOS Alert"))
        assertTrue(message.contains("unavailable"))
    }

    @Test
    fun recordSosCancelled_writesAuditEntry() = runTest {
        dispatcher.recordSosCancelled()
    }
}
