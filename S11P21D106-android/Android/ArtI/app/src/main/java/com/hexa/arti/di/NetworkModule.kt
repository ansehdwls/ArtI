package com.hexa.arti.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hexa.arti.BuildConfig
import com.hexa.arti.network.ArtWorkApi
import com.hexa.arti.util.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }


    @Singleton
    @Provides
    fun provideOkHttpClient(authInterceptor: AuthInterceptor,
                            loggingInterceptor: HttpLoggingInterceptor): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES)  // 연결 타임아웃 설정
        .readTimeout(5, TimeUnit.MINUTES)     // 읽기 타임아웃 설정
        .writeTimeout(5, TimeUnit.MINUTES)    // 쓰기 타임아웃 설정
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()
    

    @Singleton
    @Provides
    @Named("arti")
    fun provideArtIRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.SERVER_URL)  // EC2
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    @Named("arti_fast")
    fun provideArtIFastRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.FAST_SERVER_URL)  // fastAPI
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()


}
