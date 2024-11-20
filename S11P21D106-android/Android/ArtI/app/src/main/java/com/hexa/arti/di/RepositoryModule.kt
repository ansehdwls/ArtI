package com.hexa.arti.di

import com.hexa.arti.repository.ArtGalleryRepository
import com.hexa.arti.repository.ArtGalleryRepositoryImpl
import com.hexa.arti.repository.ArtWorkRepository
import com.hexa.arti.repository.ArtWorkRepositoryImpl
import com.hexa.arti.repository.ArtWorkUploadRepository
import com.hexa.arti.repository.ArtWorkUploadRepositoryImpl
import com.hexa.arti.repository.ArtistRepository
import com.hexa.arti.repository.ArtistRepositoryImpl
import com.hexa.arti.repository.HomeRepository
import com.hexa.arti.repository.HomeRepositoryImpl
import com.hexa.arti.repository.InstagramRepository
import com.hexa.arti.repository.InstagramRepositoryImpl
import com.hexa.arti.repository.LoginRepository
import com.hexa.arti.repository.LoginRepositoryImpl
import com.hexa.arti.repository.MemberRepository
import com.hexa.arti.repository.MemberRepositoryImpl
import com.hexa.arti.repository.MusicRepository
import com.hexa.arti.repository.MusicRepositoryImpl
import com.hexa.arti.repository.SignUpRepository
import com.hexa.arti.repository.SignUpRepositoryImpl
import com.hexa.arti.repository.UserRepository
import com.hexa.arti.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindArtWorkRepository(
        artWorkRepositoryImpl: ArtWorkRepositoryImpl
    ): ArtWorkRepository

    @Singleton
    @Binds
    fun bindArtistRepository(
        artistRepositoryImpl: ArtistRepositoryImpl
    ): ArtistRepository

    @Singleton
    @Binds
    fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Singleton
    @Binds
    fun bindSignUpRepository(
        signUpRepositoryImpl: SignUpRepositoryImpl
    ): SignUpRepository

    @Singleton
    @Binds
    fun bindArtWorkUploadRepository(
        artWorkUploadRepositoryImpl: ArtWorkUploadRepositoryImpl
    ): ArtWorkUploadRepository

    @Singleton
    @Binds
    fun bindArtGalleryRepository(
        artGalleryRepositoryImpl: ArtGalleryRepositoryImpl
    ): ArtGalleryRepository

    @Singleton
    @Binds
    fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository

    @Singleton
    @Binds
    fun bindMemberRepository(
        memberRepositoryImpl: MemberRepositoryImpl
    ): MemberRepository

    @Singleton
    @Binds
    fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Singleton
    @Binds
    fun bindMusicRepository(
        musicRepositoryImpl: MusicRepositoryImpl
    ): MusicRepository

    @Singleton
    @Binds
    fun bindInstagramRepository(
        instagramRepositoryImpl: InstagramRepositoryImpl
    ): InstagramRepository
}