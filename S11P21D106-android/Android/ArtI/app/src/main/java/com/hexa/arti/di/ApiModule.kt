package com.hexa.arti.di

import com.hexa.arti.network.ArtWorkApi
import com.hexa.arti.network.ArtWorkUpload
import com.hexa.arti.network.ArtistApi
import com.hexa.arti.network.GalleryApi
import com.hexa.arti.network.HomeApi
import com.hexa.arti.network.InstagramApi
import com.hexa.arti.network.LoginApi
import com.hexa.arti.network.MemberApi
import com.hexa.arti.network.MusicApi
import com.hexa.arti.network.SignUpApi
import com.hexa.arti.network.UserAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Singleton
    @Provides
    fun provideArtiApiService(@Named("arti") retrofit: Retrofit): ArtWorkApi =
        retrofit.create(ArtWorkApi::class.java)

    @Singleton
    @Provides
    fun provideArtistApiService(@Named("arti") retrofit: Retrofit): ArtistApi =
        retrofit.create(ArtistApi::class.java)

    @Singleton
    @Provides
    fun provideLoginApiService(@Named("arti") retrofit: Retrofit): LoginApi =
        retrofit.create(LoginApi::class.java)

    @Singleton
    @Provides
    fun provideSignApiService(@Named("arti") retrofit: Retrofit): SignUpApi =
        retrofit.create(SignUpApi::class.java)


    @Singleton
    @Provides
    fun provideGalleryApiService(@Named("arti") retrofit: Retrofit): GalleryApi =
        retrofit.create(GalleryApi::class.java)


    @Singleton
    @Provides
    fun provideArtWorkUploadService(@Named("arti_fast") retrofit: Retrofit): ArtWorkUpload =
        retrofit.create(ArtWorkUpload::class.java)

    @Singleton
    @Provides
    fun provideHomeApiService(@Named("arti_fast") retrofit: Retrofit): HomeApi =
        retrofit.create(HomeApi::class.java)

    @Singleton
    @Provides
    fun provideMemberApiService(@Named("arti") retrofit: Retrofit): MemberApi =
        retrofit.create(MemberApi::class.java)

    @Singleton
    @Provides
    fun provideUserApiService(@Named("arti") retrofit: Retrofit): UserAPI =
        retrofit.create(UserAPI::class.java)

    @Singleton
    @Provides
    fun provideMusicApiService(@Named("arti_fast") retrofit: Retrofit): MusicApi =
        retrofit.create(MusicApi::class.java)

    @Singleton
    @Provides  
    fun provideInstagramApiService(@Named("arti") retrofit: Retrofit): InstagramApi =
        retrofit.create(InstagramApi::class.java)
}