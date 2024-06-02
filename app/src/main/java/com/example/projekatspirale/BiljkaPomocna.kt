package com.example.projekatspirale

import com.google.gson.annotations.SerializedName

data class BiljkaPomocna(
    @SerializedName("id") val id : Int,
    @SerializedName("common_name") val commonName : String,
    @SerializedName("slug") val slug : String,
    @SerializedName("scientific_name") val scientificName : String,
    @SerializedName("family") val family : String
)
