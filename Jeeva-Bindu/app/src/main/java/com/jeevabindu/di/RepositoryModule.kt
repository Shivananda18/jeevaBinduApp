package com.jeevabindu.di

import com.jeevabindu.data.local.ThemePreferences
import com.jeevabindu.data.repository.AdminRepositoryImpl
import com.jeevabindu.data.repository.AlertRepositoryImpl
import com.jeevabindu.data.repository.AuthRepositoryImpl
import com.jeevabindu.data.repository.DonorRepositoryImpl
import com.jeevabindu.data.repository.NotificationRepositoryImpl
import com.jeevabindu.domain.repository.AdminRepository
import com.jeevabindu.domain.repository.AlertRepository
import com.jeevabindu.domain.repository.AuthRepository
import com.jeevabindu.domain.repository.DonorRepository
import com.jeevabindu.domain.repository.NotificationRepository
import com.jeevabindu.domain.repository.ThemeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    @Binds @Singleton abstract fun bindDonorRepository(impl: DonorRepositoryImpl): DonorRepository
    @Binds @Singleton abstract fun bindAlertRepository(impl: AlertRepositoryImpl): AlertRepository
    @Binds @Singleton abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository
    @Binds @Singleton abstract fun bindAdminRepository(impl: AdminRepositoryImpl): AdminRepository
    @Binds @Singleton abstract fun bindThemeRepository(impl: ThemePreferences): ThemeRepository
}
