package com.gourav.competrace.app_core.data.di

import android.content.Context
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.data.network.CodeforcesApiService
import com.gourav.competrace.app_core.data.network.KontestsApiService
import com.gourav.competrace.contests.data.ContestAlarmScheduler
import com.gourav.competrace.contests.data.ContestContestAlarmSchedulerImpl
import com.gourav.competrace.contests.data.repository.ContestAlarmRepository
import com.gourav.competrace.progress.user.data.repository.UserRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences =
        UserPreferences(context)

    @Provides
    @Singleton
    fun provideContestAlarmRepository(@ApplicationContext context: Context): ContestAlarmRepository =
        ContestAlarmRepository(context)

    @Provides
    @Singleton
    fun provideContestAlarmScheduler(@ApplicationContext context: Context): ContestAlarmScheduler =
        ContestContestAlarmSchedulerImpl(context)

    @Provides
    @Singleton
    fun provideUserRepository(@ApplicationContext context: Context): UserRepository =
        UserRepository(context)

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideCodeforcesApiService(moshi: Moshi): CodeforcesApiService = Retrofit.Builder()
        .baseUrl(CodeforcesApiService.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(CodeforcesApiService::class.java)

    @Provides
    @Singleton
    fun provideKontestsApiService(moshi: Moshi): KontestsApiService = Retrofit.Builder()
        .baseUrl(KontestsApiService.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(KontestsApiService::class.java)
}