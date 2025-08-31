package com.knowva.app.data.local

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import okio.Path.Companion.toPath

interface AuthTokenStorage {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    fun getAccessTokenFlow(): Flow<String?>
    suspend fun clearTokens()
}

class AuthTokenStorageImpl : AuthTokenStorage {

    private val tokensStore: KStore<AuthTokens> = storeOf(
        file = "auth_tokens.json".toPath(),
        default = AuthTokens()
    )

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        tokensStore.set(
            AuthTokens(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        )
    }

    override suspend fun getAccessToken(): String? {
        return tokensStore.get()?.accessToken
    }

    override suspend fun getRefreshToken(): String? {
        return tokensStore.get()?.refreshToken
    }

    override fun getAccessTokenFlow(): Flow<String?> {
        return tokensStore.updates.map { it?.accessToken }
    }

    override suspend fun clearTokens() {
        tokensStore.delete()
    }
}

@Serializable
private data class AuthTokens(
    val accessToken: String? = null,
    val refreshToken: String? = null
)