package com.knowva.app.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.knowva.app.database.Users
import com.knowva.app.database.RefreshTokens
import com.knowva.app.database.UserSessions
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    // Debug: Log environment variables
    val databaseUrl = System.getenv("DATABASE_URL")
    val databaseUser = System.getenv("DATABASE_USER")
    val databasePassword = System.getenv("DATABASE_PASSWORD")

    println(" DEBUG: DATABASE_URL = '$databaseUrl'")
    println(" DEBUG: DATABASE_USER = '$databaseUser'")
    println(" DEBUG: DATABASE_PASSWORD = '$databasePassword'")

    // Use H2 if no DATABASE_URL is provided (for development/testing)
    val useH2 = databaseUrl.isNullOrEmpty()
    println(" DEBUG: Using H2 = $useH2")

    val config = HikariConfig().apply {
        if (useH2) {
            // Use H2 for development/testing when no DATABASE_URL is set
            driverClassName = "org.h2.Driver"
            jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
            username = ""
            password = ""
            println("🗄️  Using H2 in-memory database for development")
        } else {
            // Use PostgreSQL for production
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = System.getenv("DATABASE_URL")!!
            username = System.getenv("DATABASE_USER") ?: "postgres"
            password = System.getenv("DATABASE_PASSWORD") ?: "password"
            println("🗄️  Using PostgreSQL database: ${System.getenv("DATABASE_URL")}")
        }
        maximumPoolSize = 3
    }

    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)

    // Create tables if they don't exist
    transaction {
        SchemaUtils.create(Users, RefreshTokens, UserSessions)
    }
}