package com.example.projekatspirale

import android.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import kotlin.math.min


class Converters {
    @TypeConverter
    fun fromMedicinskeKoristiList(value: String): List<MedicinskaKorist> {
        return value.split(",").map { MedicinskaKorist.valueOf(it) }
    }

    @TypeConverter
    fun toMedicinskeKoristiList(medicinskeKoristi: List<MedicinskaKorist>): String {
        return medicinskeKoristi.joinToString(",") { it.name }
    }

    @TypeConverter
    fun fromKlimatskiTipoviList(value: String): List<KlimatskiTip> {
        return value.split(",").map { KlimatskiTip.valueOf(it) }
    }

    @TypeConverter
    fun toKlimatskiTipoviList(klimatskiTipovi: List<KlimatskiTip>): String {
        return klimatskiTipovi.joinToString(",") { it.name }
    }

    @TypeConverter
    fun fromZemljisniTipoviList(value: String): List<Zemljiste> {
        return value.split(",").map { Zemljiste.valueOf(it) }
    }

    @TypeConverter
    fun toZemljisniTipoviList(zemljisniTipovi: List<Zemljiste>): String {
        return zemljisniTipovi.joinToString(",") { it.name }
    }

    @TypeConverter
    fun fromJelaList(value: String): List<String> {
        return value.split(",")
    }

    @TypeConverter
    fun toJelaList(zemljisniTipovi: List<String>): String {
        return zemljisniTipovi.joinToString(",") { it }
    }

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): String {
        val resizedBmp = Bitmap.createBitmap(bitmap, 0, 0, min(800,bitmap.width), min(800,bitmap.height))
        val outputStream = ByteArrayOutputStream()
        resizedBmp.compress(Bitmap.CompressFormat.PNG, 70, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    @TypeConverter
    fun toBitmap(encodedString: String): Bitmap {
        val byteArray = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}