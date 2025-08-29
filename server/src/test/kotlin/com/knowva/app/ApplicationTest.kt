package com.knowva.app

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
    application {
            module()
        }

        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)

        // The response should contain our service information
        val responseText = response.bodyAsText()
        assertTrue(responseText.contains("Knowva API"))
        assertTrue(responseText.contains("healthy"))
    }

    @Test
    fun testHealthCheck() = testApplication {
    application {
            module()
        }

        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)

        val responseText = response.bodyAsText()
        assertTrue(responseText.contains("healthy"))
    }
}