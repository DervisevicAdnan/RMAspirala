package com.example.projekatspirale

import com.google.gson.annotations.SerializedName

data class BiljkaPomocnaDetaljno (
    @SerializedName("id") val id : Int,
    @SerializedName("common_name") val commonName : String,
    @SerializedName("slug") val slug : String,
    @SerializedName("scientific_name") val scientificName : String,
    @SerializedName("image_url") val imageUrl : String,
    @SerializedName("main_species") val mainSpecies: MainSpecies?,
    @SerializedName("family") val family : Family?
)
data class Growth(
    @SerializedName("light") val light: Int?,
    @SerializedName("atmospheric_humidity") val atmosphericHumidity: Int?,
    @SerializedName("soil_texture") val soilTexture: Int?
)
data class Specifications(
    @SerializedName("toxicity") val toxicity: String?
)
data class MainSpecies(
    @SerializedName("id") val id : Int,
    @SerializedName("common_name") val commonName : String?,
    @SerializedName("slug") val slug : String,
    @SerializedName("scientific_name") val scientificName : String,
    @SerializedName("edible") val edible : Boolean?,
    @SerializedName("specifications") val specifications: Specifications?,
    @SerializedName("growth") val growth: Growth?
)
data class Family(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?
)