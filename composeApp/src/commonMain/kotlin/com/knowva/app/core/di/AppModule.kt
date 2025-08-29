package com.knowva.app.core.di

import org.koin.dsl.module
import com.knowva.app.presentation.features.home.HomeViewModel

/**
 * Main app module with dependency injection configuration
 */
val appModule = module {

    // Core utilities - commented out until implemented
    // includes(networkModule, storageModule, repositoryModule, useCaseModule)

    // ViewModels
    factory { HomeViewModel() }
}

val networkModule = module {
    // Network dependencies will be added here
    // single { createHttpClient() }
    // single { ApiService(get()) }
}

val storageModule = module {
    // Storage dependencies will be added here
    // single { LocalStorageService() }
    // single { PreferencesManager() }
}

val repositoryModule = module {
    // Repository implementations will be added here
    // single<UserRepository> { UserRepositoryImpl(get(), get()) }
    // single<GameRepository> { GameRepositoryImpl(get(), get()) }
}

val useCaseModule = module {
    // Use cases will be added here
    // factory { GetUserProfileUseCase(get()) }
    // factory { StartGameUseCase(get()) }
}

/**
 * Platform-specific modules to be defined in androidMain and iosMain
 */
expect val platformModule: org.koin.core.module.Module