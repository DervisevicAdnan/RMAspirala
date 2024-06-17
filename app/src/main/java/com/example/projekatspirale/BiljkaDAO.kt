package com.example.projekatspirale

import android.graphics.Bitmap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Dao
interface BiljkaDAO {
    @Query("SELECT * FROM biljka")
    suspend fun getAllBiljkas(): List<Biljka>

    @Insert
    suspend fun insertAll(vararg biljka: Biljka)

    @Query("delete from biljka")
    fun deleteAllBiljkas()

    @Query("delete from biljkabitmap")
    fun deleteAllImages()

    @Update
    fun updateBiljka(biljka: Biljka)

    @Query("SELECT * FROM biljka WHERE id = :idBiljke")
    fun getBiljkaById(idBiljke: Long): List<Biljka>

    @Query("SELECT * FROM biljkabitmap WHERE idBiljke = :idBiljke")
    fun getBitmapByBiljkaId(idBiljke: Long): List<BiljkaBitmap>

    @Insert
    suspend fun insertBitmap(vararg biljkaBitmap: BiljkaBitmap)


    suspend fun saveBiljka(biljka: Biljka):Boolean{
        try{
            insertAll(biljka)
        }catch (error: Exception){
            return false
        }
        return true
    }

    suspend fun fixOfflineBiljka():Int{
        var br = 0
        var trefleDAO = TrefleDAO()
        var biljke = getAllBiljkas()
        for(biljka in biljke){
            if(biljka.onlineChecked) continue
            var fixBiljka = trefleDAO.fixData(biljka)
            fixBiljka.id = biljka.id

            if(biljka != fixBiljka){
                br++
            }

            fixBiljka.onlineChecked = true

            updateBiljka(fixBiljka)
        }
        return br
    }

    suspend fun addImage(idBiljke: Long, bitmap: Bitmap):Boolean {
        var biljke = getBiljkaById(idBiljke)
        if(biljke.isNullOrEmpty()) return false
        var slike = getBitmapByBiljkaId(idBiljke)
        if(slike.isNotEmpty()) return false
        try{
            insertBitmap(BiljkaBitmap(null, idBiljke, bitmap))
        }catch (exception: Exception){
            return false
        }
        return true
    }

    suspend fun clearData(){
        deleteAllImages()
        deleteAllBiljkas()
    }
}