package com.example.projekatspirale

import com.google.gson.annotations.SerializedName

data class GetTrefleResponse(
    @SerializedName("data") val biljke: List<BiljkaPomocna>
)
data class GetTrefleResponseDetaljno(
    @SerializedName("data") val biljka: BiljkaPomocnaDetaljno
)