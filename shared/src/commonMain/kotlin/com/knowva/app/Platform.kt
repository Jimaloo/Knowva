package com.knowva.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform