package com.example.projekatspirale

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("plants/")
    suspend fun getAllPlants(
        @Query("token") apiKey: String = BuildConfig.API_TOKEN
    ): Response<GetTrefleResponse>
    @GET("plants/")
    suspend fun getPlantsPoLatNazivu(
        @Query("filter[scientific_name]") latinskiNaziv: String,
        @Query("token") apiKey: String = BuildConfig.API_TOKEN
    ): Response<GetTrefleResponse>
    @GET("plants/search")
    suspend fun getPlantsPoFlowerColor(
        @Query("filter[flower_color]") flowerColor: String,
        @Query("q") substr: String,
        @Query("token") apiKey: String = BuildConfig.API_TOKEN
    ): Response<GetTrefleResponse>
    @GET("plants/{id}")
    suspend fun getPlantsPoId(
        @Path("id") plantId: Int,
        @Query("token") apiKey: String = BuildConfig.API_TOKEN
    ): Response<GetTrefleResponseDetaljno>
}