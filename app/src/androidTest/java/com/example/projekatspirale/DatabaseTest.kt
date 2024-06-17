package com.example.projekatspirale

import android.app.Activity
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var biljkaDao: BiljkaDAO
    private lateinit var db: BiljkaDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, BiljkaDatabase::class.java).build()
        biljkaDao = db.biljkaDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun t1_dodavanjeBiljke() {
        val biljka: Biljka = Biljka(
            naziv = "biljka1",
            porodica = "neka porodica",
            medicinskoUpozorenje = "NE DIRAJ",
            medicinskeKoristi = listOf(MedicinskaKorist.REGULACIJAPRITISKA),
            jela = listOf("Burek", "Zeljanica", "Janjetina"),
            klimatskiTipovi = listOf(KlimatskiTip.UMJERENA),
            profilOkusa = ProfilOkusaBiljke.AROMATICNO,
            zemljisniTipovi = listOf(Zemljiste.CRNICA,Zemljiste.ILOVACA)
        )

        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            biljkaDao.saveBiljka(biljka)
            val listaBiljaka = biljkaDao.getAllBiljkas()
            assertThat(listaBiljaka.size.toString(),not(equalTo("0")))
            listaBiljaka.last().id = null
            assertThat(listaBiljaka.last(), equalTo(biljka))
        }

    }
    @Test
    @Throws(Exception::class)
    fun t2_clearData() {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            var listaBiljaka = biljkaDao.getAllBiljkas()
            assertThat(listaBiljaka.size.toString(),not(equalTo("0")))
            biljkaDao.clearData()
            listaBiljaka = biljkaDao.getAllBiljkas()
            assertThat(listaBiljaka.size.toString(),equalTo("0"))
        }

    }
}