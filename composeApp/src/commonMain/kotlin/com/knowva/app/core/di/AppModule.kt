package com.knowva.app.core.di

import org.koin.dsl.module
import com.knowva.app.presentation.features.home.HomeViewModel
import com.knowva.app.presentation.features.auth.LoginViewModel
import com.knowva.app.presentation.features.auth.RegisterViewModel
import com.knowva.app.presentation.features.auth.ProfileViewModel
import com.knowva.app.data.repositories.AuthRepository
import com.knowva.app.data.repositories.AuthRepositoryImpl
import com.knowva.app.data.remote.AuthApiService
import com.knowva.app.data.remote.AuthApiServiceImpl
import com.knowva.app.data.local.AuthTokenStorage
import com.knowva.app.data.local.AuthTokenStorageImpl

/**
 * Main app module with dependency injection configuration
 */
val appModule = module {

    // Local Storage
    single<AuthTokenStorage> { AuthTokenStorageImpl() }

    // API Services (using HttpClient from platformModule)
    single<AuthApiService> { AuthApiServiceImpl(get()) }

    // Repository implementations
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    // ViewModels
    factory { HomeViewModel() }
    factory { LoginViewModel(get()) }
    factory { RegisterViewModel(get()) }
    factory { ProfileViewModel(get()) }
}

/**
 * Platform-specific modules to be defined in androidMain and iosMain
 */
expect val platformModule: org.koin.core.module.Module