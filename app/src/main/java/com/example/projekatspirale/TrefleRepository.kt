package com.example.projekatspirale

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object TrefleRepository {

    suspend fun getAllPlants() : GetTrefleResponse?{
        return withContext(Dispatchers.IO) {
            var response = ApiAdapter.retrofit.getAllPlants()
            val responseBody = response.body()
            return@withContext responseBody
        }
    }
    suspend fun getPlantsPoLatNazivu(latinskiNaziv:String): GetTrefleResponse? {
        return withContext(Dispatchers.IO) {
            var response = ApiAdapter.retrofit.getPlantsPoLatNazivu(latinskiNaziv)
            val responseBody = response.body()
            return@withContext responseBody
        }
    }
    suspend fun getPlantsPoId(id:Int): GetTrefleResponseDetaljno? {
        return withContext(Dispatchers.IO) {
            var response = ApiAdapter.retrofit.getPlantsPoId(id)
            val responseBody = response.body()
            return@withContext responseBody
        }
    }
    suspend fun getPlantsPoFlowerColor(flowerColor: String, substr: String): GetTrefleResponse? {
        return withContext(Dispatchers.IO) {
            var response = ApiAdapter.retrofit.getPlantsPoFlowerColor(flowerColor,substr)
            val responseBody = response.body()
            return@withContext responseBody
        }
    }
}