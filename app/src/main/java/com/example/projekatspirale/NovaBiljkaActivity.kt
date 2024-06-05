package com.example.projekatspirale

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NovaBiljkaActivity : AppCompatActivity() {
    private lateinit var nazivET : EditText
    private lateinit var porodicaET : EditText
    private lateinit var medicinskoUpozorenjeET : EditText
    private lateinit var jeloET : EditText

    private lateinit var dodajJeloBtn : Button
    private lateinit var dodajBiljkuBtn : Button
    private lateinit var uslikajBiljkuBtn : Button

    private lateinit var slikaIV : ImageView

    private lateinit var medicinskaKoristLV : ListView
    private lateinit var klimatskiTipLV : ListView
    private lateinit var zemljisniTipLV : ListView
    private lateinit var profilOkusaLV : ListView
    private lateinit var jelaLV : ListView

    private val medicinskeKoristiVrijednosti = arrayListOf<String>()
    private lateinit var adapterMedicinskaKorist : ArrayAdapter<String>
    private val zemljisniTipoviVrijednosti = arrayListOf<String>()
    private lateinit var adapterZemljisne : ArrayAdapter<String>
    private val klimatskiTipoviVrijednosti = arrayListOf<String>()
    private lateinit var adapterKlimatski : ArrayAdapter<String>
    private val profiliOkusaVrijednosti = arrayListOf<String>()
    private lateinit var adapterOkusi : ArrayAdapter<String>
    private val jelaVrijednosti = arrayListOf<String>()
    private lateinit var adapterJela : ArrayAdapter<String>

    val REQUEST_IMAGE_CAPTURE = 1

    private var kliknutoJelo : Int? = null

    val slikajSlikuLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            slikaIV.setImageBitmap(imageBitmap)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nova_biljka)

        nazivET = findViewById(R.id.nazivET)
        porodicaET = findViewById(R.id.porodicaET)
        medicinskoUpozorenjeET = findViewById(R.id.medicinskoUpozorenjeET)
        jeloET = findViewById(R.id.jeloET)
        dodajJeloBtn = findViewById(R.id.dodajJeloBtn)
        dodajBiljkuBtn = findViewById(R.id.dodajBiljkuBtn)
        uslikajBiljkuBtn = findViewById(R.id.uslikajBiljkuBtn)
        slikaIV = findViewById(R.id.slikaIV)

        medicinskaKoristLV = findViewById(R.id.medicinskaKoristLV)
        for(i in MedicinskaKorist.entries){
            medicinskeKoristiVrijednosti.add(i.opis)
        }
        adapterMedicinskaKorist = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, medicinskeKoristiVrijednosti)
        medicinskaKoristLV.adapter = adapterMedicinskaKorist

        zemljisniTipLV = findViewById(R.id.zemljisniTipLV)
        for(i in Zemljiste.entries){
            zemljisniTipoviVrijednosti.add(i.naziv)
        }
        adapterZemljisne = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, zemljisniTipoviVrijednosti)
        zemljisniTipLV.adapter = adapterZemljisne

        klimatskiTipLV = findViewById(R.id.klimatskiTipLV)
        for(i in KlimatskiTip.entries){
            klimatskiTipoviVrijednosti.add(i.opis)
        }
        adapterKlimatski = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, klimatskiTipoviVrijednosti)
        klimatskiTipLV.adapter = adapterKlimatski

        profilOkusaLV = findViewById(R.id.profilOkusaLV)
        for(i in ProfilOkusaBiljke.entries){
            profiliOkusaVrijednosti.add(i.opis)
        }
        adapterOkusi = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, profiliOkusaVrijednosti)
        profilOkusaLV.adapter = adapterOkusi

        jelaLV = findViewById(R.id.jelaLV)
        adapterJela = ArrayAdapter(this, android.R.layout.simple_list_item_1, jelaVrijednosti)
        jelaLV.adapter = adapterJela
        jelaLV.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                jeloET.setText(jelaVrijednosti[position])
                kliknutoJelo = position
                dodajJeloBtn.setText("Izmijeni jelo")
            }
        })

        dodajBiljkuBtn.setOnClickListener {
            dodajBiljku()
        }

        dodajJeloBtn.setOnClickListener {
            dodajJelo()
        }

        uslikajBiljkuBtn.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun dodajBiljku(){
        var imaGresaka = false
        var imaBtnGreska = false
        if(nazivET.text.toString().length < 2 || nazivET.text.toString().length > 40){
            nazivET.setError("Dužina naziva biljke mora biti između 2 i 40 karaktera")
            imaGresaka = true
        }
        var otvorenaZagrada = -1
        var zatvorenaZagrada = -1
        var pomNaziv = nazivET.text.toString()
        for(i in pomNaziv.indices){
            if(pomNaziv[i] == '(') otvorenaZagrada = i
            if(pomNaziv[i] == ')') {
                zatvorenaZagrada = i
                break
            }
        }
        if(otvorenaZagrada < 0 || zatvorenaZagrada < 0 || zatvorenaZagrada-otvorenaZagrada<2){
            if(nazivET.error != null) nazivET.setError(nazivET.error.toString()+"\nNaziv mora biti u formatu: naziv (latinski naziv)")
            else nazivET.setError("Naziv mora biti u formatu: naziv (latinski naziv)")
            imaGresaka = true
        }
        if(porodicaET.text.toString().length < 2 || porodicaET.text.toString().length > 20){
            porodicaET.setError("Dužina naziva porodice mora biti između 2 i 20 karaktera")
            imaGresaka = true
        }
        if(medicinskoUpozorenjeET.text.toString().length < 2 || medicinskoUpozorenjeET.text.toString().length > 20){
            medicinskoUpozorenjeET.setError("Dužina medicinskog upozorenja mora biti između 2 i 20 karaktera")
            imaGresaka = true
        }

        dodajBiljkuBtn.setError(null)

        if(medicinskaKoristLV.checkedItemCount == 0 || klimatskiTipLV.checkedItemCount == 0
            || zemljisniTipLV.checkedItemCount == 0 || profilOkusaLV.checkedItemCount == 0){
            dodajBiljkuBtn.setError("Nije selektovan item u svim listama")
            imaGresaka = true
            imaBtnGreska = true
        }

        if(jelaVrijednosti.size == 0){
            if(dodajBiljkuBtn.error != null) dodajBiljkuBtn.setError(dodajBiljkuBtn.error.toString()+"\nNije dodano ni jedno jelo")
            else dodajBiljkuBtn.setError("Nije dodano ni jedno jelo")
            imaGresaka = true
            imaBtnGreska = true
        }

        if(!imaBtnGreska) dodajBiljkuBtn.setError(null)
        else{
            dodajBiljkuBtn.isFocusableInTouchMode = true
            dodajBiljkuBtn.requestFocus()
            dodajBiljkuBtn.isFocusableInTouchMode = false
        }
        if(imaGresaka) return

        val listMedicinskeKoristi = arrayListOf<MedicinskaKorist>()
        var medKoristClickedPos = medicinskaKoristLV.checkedItemPositions
        for(i in 0..<medicinskaKoristLV.checkedItemCount){
            if(medKoristClickedPos.valueAt(i))
                listMedicinskeKoristi.add(MedicinskaKorist.entries[medKoristClickedPos.keyAt(i)])
        }
        Log.e("moje1",medicinskaKoristLV.checkedItemIds.toList().toString())

        val listKlimatskiTip = arrayListOf<KlimatskiTip>()
        var klimTipClickedPos = klimatskiTipLV.checkedItemPositions
        for(i in 0..<klimatskiTipLV.checkedItemCount){
            if(klimTipClickedPos.valueAt(i))
                listKlimatskiTip.add(KlimatskiTip.entries[klimTipClickedPos.keyAt(i)])
        }
        Log.e("moje2",klimatskiTipLV.checkedItemPositions.toString())

        val listZemljiste = arrayListOf<Zemljiste>()
        var zemTipClickedPos = zemljisniTipLV.checkedItemPositions
        for(i in 0..<zemljisniTipLV.checkedItemCount){
            if(zemTipClickedPos.valueAt(i))
                listZemljiste.add(Zemljiste.entries[zemTipClickedPos.keyAt(i)])
        }
        Log.e("moje",zemljisniTipLV.checkedItemIds.toList().toString())

        var novaBiljka = Biljka(
            nazivET.text.toString(),
            porodicaET.text.toString(),
            medicinskoUpozorenjeET.text.toString(),
            listMedicinskeKoristi,
            ProfilOkusaBiljke.entries[profilOkusaLV.selectedItemId.toInt()],
            jelaVrijednosti,
            listKlimatskiTip,
            listZemljiste
            )

        var trefleDAO = TrefleDAO()
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Create a new coroutine on the UI thread
        scope.launch {
            dodajBiljkuUListu(trefleDAO.fixData(novaBiljka))
            //val intent = Intent(this, MainActivity::class.java).apply {}
            //startActivity(intent)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun dodajJelo(){
        if (kliknutoJelo == null) {
            if(jeloET.text.toString().length < 2 || jeloET.text.toString().length > 20){
                jeloET.setError("Dužina naziva jela mora biti između 2 i 20 karaktera")
                return
            }
            for (i in 0..jelaVrijednosti.size-1){
                if(jeloET.text.toString().lowercase() == jelaVrijednosti[i].lowercase()){
                    jeloET.setError("Jelo već postoji u listi")
                    return
                }
            }
            jelaVrijednosti.add(jeloET.text.toString())
        }else{
            if(jeloET.text.toString().isNullOrEmpty()){
                jelaVrijednosti.removeAt(kliknutoJelo!!)
            }
            else{
                if(jeloET.text.toString().length < 2 || jeloET.text.toString().length > 20){
                    jeloET.setError("Dužina naziva jela mora biti između 2 i 20 karaktera")
                    return
                }
                for (i in 0..<jelaVrijednosti.size){
                    if(jeloET.text.toString().lowercase() == jelaVrijednosti[i].lowercase() && i != kliknutoJelo){
                        jeloET.setError("Jelo već postoji u listi")
                        return
                    }
                }
                jelaVrijednosti[kliknutoJelo!!] = jeloET.text.toString()
            }

            dodajJeloBtn.setText("Dodaj jelo")
            kliknutoJelo = null
        }
        jeloET.setText("")
        adapterJela.notifyDataSetChanged()
    }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            slikajSlikuLauncher.launch(takePictureIntent)
            //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {

        }
    }
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            slikaIV.setImageBitmap(imageBitmap)
        }
    }*/
}