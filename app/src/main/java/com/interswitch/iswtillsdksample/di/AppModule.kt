package com.interswitch.iswtillsdksample.di

import android.content.Context
import com.interswitch.smart_till.IswTillManagerSDK
import com.interswitch.iswtillsdksample.data.repository.AppRepositoryImpl
import com.interswitch.iswtillsdksample.data.repository.IAppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTillManagerSDK(@ApplicationContext context: Context): IswTillManagerSDK {
        return IswTillManagerSDK.createInstance(context)
    }

    @Provides
    @Singleton
    fun provideRepository(
        tillManagerSDK: IswTillManagerSDK,
        @ApplicationContext context: Context
    ): IAppRepository {
        return AppRepositoryImpl(tillManagerSDK, context)
    }
}
