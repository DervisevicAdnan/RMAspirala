package com.example.projekatspirale

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class TrefleDAO() {
    private lateinit var context: Context
    private var drawable : Drawable? = null
    private var defaultBitmap :Bitmap? = null

    fun setContext(cont:Context){
        context = cont
        drawable = ContextCompat.getDrawable(context, R.drawable.test)
        defaultBitmap = (drawable as BitmapDrawable).bitmap
    }

    private suspend fun searchPoLatNazivu(latinskiNaziv: String): Int? {
        return try {
            val result = TrefleRepository.getPlantsPoLatNazivu(latinskiNaziv)
            result?.biljke?.firstOrNull()?.id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun searchPoFlowerColor(flowerColor: String, substr: String): GetTrefleResponse? {
        return try {
            val result = TrefleRepository.getPlantsPoFlowerColor(flowerColor, substr)
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun getPlantPoId(id: Int): BiljkaPomocnaDetaljno? {
        return try {
            val result = TrefleRepository.getPlantsPoId(id)
            result?.biljka
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getImage(biljka: Biljka): Bitmap {
        val latinskiNaziv:String = getLatinski(biljka.naziv)
        val id = searchPoLatNazivu(latinskiNaziv)
        if(id != null){
            val detaljno = getPlantPoId(id)
            if(detaljno != null){
                return withContext(Dispatchers.IO) {
                    try {
                        Glide.with(context)
                            .asBitmap()
                            .load(detaljno.imageUrl)
                            .submit()
                            .get()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        defaultBitmap!!
                    }
                }
            }
        }
        return defaultBitmap!!
    }

    suspend fun fixData(biljka: Biljka) : Biljka{
        var naziv : String
        var porodica : String
        var medicinskoUpozorenje : String
        var medicinskeKoristi : List<MedicinskaKorist>
        var profilOkusa : ProfilOkusaBiljke?
        var jela : List<String> = emptyList()
        var klimatskiTipovi : List<KlimatskiTip>
        var zemljisniTipovi : List<Zemljiste> = emptyList()

        val latinskiNaziv:String = getLatinski(biljka.naziv)
        val id = searchPoLatNazivu(latinskiNaziv)
        if(id != null){
            val detaljno = getPlantPoId(id)
            if(detaljno != null){

                if(detaljno.family!=null && detaljno.family.name!=null && detaljno.family.name.lowercase()!=biljka.porodica.lowercase()){
                    porodica = detaljno.family.name
                }else{
                    porodica = biljka.porodica
                }

                medicinskoUpozorenje = biljka.medicinskoUpozorenje

                if(detaljno.mainSpecies != null && detaljno.mainSpecies.edible != null && !detaljno.mainSpecies.edible) {
                    if(!biljka.medicinskoUpozorenje.contains("NIJE JESTIVO",true)){
                        medicinskoUpozorenje += " NIJE JESTIVO"
                    }
                    if(!biljka.jela.isEmpty()){
                        jela = emptyList()
                    }
                }else{
                    jela = biljka.jela
                }

                if(detaljno.mainSpecies != null && detaljno.mainSpecies.specifications != null && detaljno.mainSpecies.specifications.toxicity != null
                    && !medicinskoUpozorenje.contains("TOKSIČNO",true)){
                        medicinskoUpozorenje += " TOKSIČNO"
                }

                if(detaljno.mainSpecies != null && detaljno.mainSpecies.growth != null && detaljno.mainSpecies.growth.soilTexture != null){
                    if(detaljno.mainSpecies.growth.soilTexture == 9){
                        zemljisniTipovi = listOf(Zemljiste.SLJUNKOVITO)
                    }else if(detaljno.mainSpecies.growth.soilTexture == 10){
                        zemljisniTipovi = listOf(Zemljiste.KRECNJACKO)
                    }else if(detaljno.mainSpecies.growth.soilTexture == 1 || detaljno.mainSpecies.growth.soilTexture == 2){
                        zemljisniTipovi = listOf(Zemljiste.GLINENO)
                    }else if(detaljno.mainSpecies.growth.soilTexture == 3 || detaljno.mainSpecies.growth.soilTexture == 4){
                        zemljisniTipovi = listOf(Zemljiste.PJESKOVITO)
                    }else if(detaljno.mainSpecies.growth.soilTexture == 5 || detaljno.mainSpecies.growth.soilTexture == 6){
                        zemljisniTipovi = listOf(Zemljiste.ILOVACA)
                    }else if(detaljno.mainSpecies.growth.soilTexture == 7 || detaljno.mainSpecies.growth.soilTexture == 8){
                        zemljisniTipovi = listOf(Zemljiste.CRNICA)
                    }
                }else{
                    zemljisniTipovi = biljka.zemljisniTipovi
                }

                if(detaljno.mainSpecies != null && detaljno.mainSpecies.growth != null && detaljno.mainSpecies.growth.light != null
                    && detaljno.mainSpecies.growth.atmosphericHumidity != null){
                    var pomKlime = ArrayList<KlimatskiTip>()
                    if(detaljno.mainSpecies.growth.light >= 6 && detaljno.mainSpecies.growth.light <= 9){
                        if(detaljno.mainSpecies.growth.atmosphericHumidity >= 1 && detaljno.mainSpecies.growth.atmosphericHumidity <= 5){
                            pomKlime.add(KlimatskiTip.SREDOZEMNA)
                        }
                        if(detaljno.mainSpecies.growth.atmosphericHumidity >= 5 && detaljno.mainSpecies.growth.atmosphericHumidity <= 8){
                            pomKlime.add(KlimatskiTip.SUBTROPSKA)
                        }
                    }
                    if(detaljno.mainSpecies.growth.light >= 8 && detaljno.mainSpecies.growth.light <= 10){
                        if(detaljno.mainSpecies.growth.atmosphericHumidity >= 7 && detaljno.mainSpecies.growth.atmosphericHumidity <= 10){
                            pomKlime.add(KlimatskiTip.TROPSKA)
                        }
                    }
                    if(detaljno.mainSpecies.growth.light >= 4 && detaljno.mainSpecies.growth.light <= 7){
                        if(detaljno.mainSpecies.growth.atmosphericHumidity >= 3 && detaljno.mainSpecies.growth.atmosphericHumidity <= 7){
                            pomKlime.add(KlimatskiTip.UMJERENA)
                        }
                    }
                    if(detaljno.mainSpecies.growth.light >= 7 && detaljno.mainSpecies.growth.light <= 9){
                        if(detaljno.mainSpecies.growth.atmosphericHumidity >= 1 && detaljno.mainSpecies.growth.atmosphericHumidity <= 2){
                            pomKlime.add(KlimatskiTip.SUHA)
                        }
                    }
                    if(detaljno.mainSpecies.growth.light >= 0 && detaljno.mainSpecies.growth.light <= 5){
                        if(detaljno.mainSpecies.growth.atmosphericHumidity >= 3 && detaljno.mainSpecies.growth.atmosphericHumidity <= 7){
                            pomKlime.add(KlimatskiTip.PLANINSKA)
                        }
                    }
                    klimatskiTipovi = pomKlime
                }
                else{
                    klimatskiTipovi = biljka.klimatskiTipovi
                }

                naziv = biljka.naziv
                medicinskeKoristi = biljka.medicinskeKoristi
                profilOkusa = biljka.profilOkusa

                return Biljka(naziv, porodica, medicinskoUpozorenje, medicinskeKoristi, profilOkusa, jela, klimatskiTipovi, zemljisniTipovi)

            }
        }
        return biljka
    }

    suspend fun getPlantsWithFlowerColor(flowerColor: String, substr: String): List<Biljka> {
        val result = searchPoFlowerColor(flowerColor, substr)
        var lista = ArrayList<Biljka>()
        if(result != null){
            for(biljka in result.biljke) {
                val detaljno = getPlantPoId(biljka.id)
                if (detaljno != null) {
                    val biljcica = BiljkaFromPomocnaDetaljno(detaljno)
                    //Log.e("bleaj",biljcica.toString())
                    if(biljcica != null){
                        lista.add(biljcica)
                    }
                }
            }
            return lista
        }
        return emptyList()
    }


    private fun BiljkaFromPomocnaDetaljno(detaljno: BiljkaPomocnaDetaljno) : Biljka?{
        var naziv : String = ""
        var porodica : String = ""
        var medicinskoUpozorenje : String = ""
        var medicinskeKoristi : List<MedicinskaKorist> = emptyList()
        var profilOkusa : ProfilOkusaBiljke? = ProfilOkusaBiljke.BEZUKUSNO
        var jela : List<String> = emptyList()
        var klimatskiTipovi : List<KlimatskiTip> = emptyList()
        var zemljisniTipovi : List<Zemljiste> = emptyList()
        if(detaljno != null){

            if(detaljno.family!=null && detaljno.family.name!=null){
                porodica = detaljno.family.name
            }

            if(detaljno.mainSpecies != null && detaljno.mainSpecies.edible != null && !detaljno.mainSpecies.edible) {
                medicinskoUpozorenje += " NIJE JESTIVO"
            }

            if(detaljno.mainSpecies != null && detaljno.mainSpecies.specifications != null && detaljno.mainSpecies.specifications.toxicity != null){
                medicinskoUpozorenje += " TOKSIČNO"
            }

            if(detaljno.mainSpecies != null && detaljno.mainSpecies.growth != null && detaljno.mainSpecies.growth.soilTexture != null){
                if(detaljno.mainSpecies.growth.soilTexture == 9){
                    zemljisniTipovi = listOf(Zemljiste.SLJUNKOVITO)
                }else if(detaljno.mainSpecies.growth.soilTexture == 10){
                    zemljisniTipovi = listOf(Zemljiste.KRECNJACKO)
                }else if(detaljno.mainSpecies.growth.soilTexture == 1 || detaljno.mainSpecies.growth.soilTexture == 2){
                    zemljisniTipovi = listOf(Zemljiste.GLINENO)
                }else if(detaljno.mainSpecies.growth.soilTexture == 3 || detaljno.mainSpecies.growth.soilTexture == 4){
                    zemljisniTipovi = listOf(Zemljiste.PJESKOVITO)
                }else if(detaljno.mainSpecies.growth.soilTexture == 5 || detaljno.mainSpecies.growth.soilTexture == 6){
                    zemljisniTipovi = listOf(Zemljiste.ILOVACA)
                }else if(detaljno.mainSpecies.growth.soilTexture == 7 || detaljno.mainSpecies.growth.soilTexture == 8){
                    zemljisniTipovi = listOf(Zemljiste.CRNICA)
                }
            }

            if(detaljno.mainSpecies != null && detaljno.mainSpecies.growth != null && detaljno.mainSpecies.growth.light != null
                && detaljno.mainSpecies.growth.atmosphericHumidity != null){
                var pomKlime = ArrayList<KlimatskiTip>()
                if(detaljno.mainSpecies.growth.light >= 6 && detaljno.mainSpecies.growth.light <= 9){
                    if(detaljno.mainSpecies.growth.atmosphericHumidity >= 1 && detaljno.mainSpecies.growth.atmosphericHumidity <= 5){
                        pomKlime.add(KlimatskiTip.SREDOZEMNA)
                    }
                    if(detaljno.mainSpecies.growth.atmosphericHumidity >= 5 && detaljno.mainSpecies.growth.atmosphericHumidity <= 8){
                        pomKlime.add(KlimatskiTip.SUBTROPSKA)
                    }
                }
                if(detaljno.mainSpecies.growth.light >= 8 && detaljno.mainSpecies.growth.light <= 10){
                    if(detaljno.mainSpecies.growth.atmosphericHumidity >= 7 && detaljno.mainSpecies.growth.atmosphericHumidity <= 10){
                        pomKlime.add(KlimatskiTip.TROPSKA)
                    }
                }
                if(detaljno.mainSpecies.growth.light >= 4 && detaljno.mainSpecies.growth.light <= 7){
                    if(detaljno.mainSpecies.growth.atmosphericHumidity >= 3 && detaljno.mainSpecies.growth.atmosphericHumidity <= 7){
                        pomKlime.add(KlimatskiTip.UMJERENA)
                    }
                }
                if(detaljno.mainSpecies.growth.light >= 7 && detaljno.mainSpecies.growth.light <= 9){
                    if(detaljno.mainSpecies.growth.atmosphericHumidity >= 1 && detaljno.mainSpecies.growth.atmosphericHumidity <= 2){
                        pomKlime.add(KlimatskiTip.SUHA)
                    }
                }
                if(detaljno.mainSpecies.growth.light >= 0 && detaljno.mainSpecies.growth.light <= 5){
                    if(detaljno.mainSpecies.growth.atmosphericHumidity >= 3 && detaljno.mainSpecies.growth.atmosphericHumidity <= 7){
                        pomKlime.add(KlimatskiTip.PLANINSKA)
                    }
                }
                klimatskiTipovi = pomKlime
            }

            if(detaljno.commonName != null) naziv = detaljno.commonName
            if(detaljno.scientificName != null) naziv += " ("+detaljno.scientificName+")"
            return Biljka(naziv, porodica, medicinskoUpozorenje, medicinskeKoristi, profilOkusa, jela, klimatskiTipovi, zemljisniTipovi)

        }

        return null
    }

    private fun getLatinski(naziv:String):String{
        var lat:String = ""
        var zagrade = false
        for(i in naziv.indices){
            if(naziv[i]==')') break
            if(zagrade) lat += naziv[i]
            if(naziv[i]=='(') zagrade = true
        }
        return lat
    }
}