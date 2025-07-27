package com.example.guardiannetapp.DI

import com.example.guardiannetapp.API.AuthApi
import com.example.guardiannetapp.Repo.Repo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent ::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit{
        return Retrofit.Builder()
            .baseUrl("http://192.168.82.9:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApi): Repo =
        Repo(api)
}