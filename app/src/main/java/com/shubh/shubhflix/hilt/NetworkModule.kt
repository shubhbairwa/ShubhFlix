package com.shubh.shubhflix.hilt

import com.shubh.shubhflix.interfaces.PexelsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://pixabay.com/api/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

            .client(
                OkHttpClient.Builder()
                    /*.addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader(
                                "Authorization",
                                "Bearer BfFPYi4WneGA1C8akhXHB1ScchqwVJVowSOC83wfxOOxQgeLxXyF1WHx"
                            )
                            .build()
                        chain.proceed(request)
                    }*/
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })

                    .build()
            )
            .build()
    }

    @Provides
    @Singleton
    fun providePexelsApi(retrofit: Retrofit): PexelsApi {
        return retrofit.create(PexelsApi::class.java)
    }
}
