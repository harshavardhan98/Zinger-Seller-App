package com.food.ordering.zinger.seller.di

import com.food.ordering.zinger.seller.BuildConfig
import com.food.ordering.zinger.seller.data.retofit.AuthInterceptor
import com.food.ordering.zinger.seller.data.retofit.ItemRepository
import com.food.ordering.zinger.seller.data.retofit.OrderRepository
import com.food.ordering.zinger.seller.data.retofit.ShopRepository
import com.google.gson.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


val networkModule = module {
    single { AuthInterceptor(get(),get()) }
    single { provideRetrofit(get()) }
    single { ItemRepository(get()) }
    single { ShopRepository(get()) }
    single { OrderRepository(get())}
}

fun provideRetrofit(authInterceptor: AuthInterceptor): Retrofit {

    val gson = GsonBuilder().registerTypeAdapter(Date::class.java, DateTypeDeserializer()).create()

    return Retrofit.Builder()
        .baseUrl("https://food-backend-ssn.herokuapp.com")
        .client(provideOkHttpClient(authInterceptor))
        .addConverterFactory(GsonConverterFactory.create(gson)).build()

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

class DateTypeDeserializer : JsonDeserializer<Date> {

    private val DATE_FORMATS = arrayOf("dd/MM/yyyy HH:mm:ss", "HH:mm:ss")

    @Throws(JsonParseException::class)
    override fun deserialize(jsonElement: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date {
        for (format in DATE_FORMATS) {
            try {
                return SimpleDateFormat(format).parse(jsonElement.asString)
            } catch (e: Exception) {
            }
        }
        throw JsonParseException("Unparseable date: \"" + jsonElement.asString)
    }
}