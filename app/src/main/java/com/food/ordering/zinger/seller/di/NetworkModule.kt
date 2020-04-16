package com.food.ordering.zinger.seller.di

import com.food.ordering.zinger.seller.BuildConfig
import com.food.ordering.zinger.seller.data.retofit.AuthInterceptor
import com.food.ordering.zinger.seller.data.retofit.ItemRepository
import com.food.ordering.zinger.seller.data.retofit.OrderRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { AuthInterceptor(get(),get()) }
    single { provideRetrofit(get()) }
    single { ItemRepository(get())}
}

fun provideRetrofit(authInterceptor: AuthInterceptor): Retrofit {
    return Retrofit.Builder().baseUrl("https://food-backend-ssn.herokuapp.com").client(provideOkHttpClient(authInterceptor))
        .addConverterFactory(GsonConverterFactory.create()).build()
}

fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
    val builder = OkHttpClient()
        .newBuilder()
        .addInterceptor(authInterceptor)

    if (BuildConfig.DEBUG) {
        val requestInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addNetworkInterceptor(requestInterceptor)
    }
    return builder.build()
}