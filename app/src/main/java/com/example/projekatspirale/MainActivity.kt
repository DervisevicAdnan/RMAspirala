package com.example.projekatspirale

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var spinner : Spinner
    private var modovi = listOf<String>("Medicinski mod", "Kuharski mod", "Botanički mod")
    private lateinit var spinnerAdapter : ArrayAdapter<String>
    private lateinit var resetBtn: Button
    private lateinit var novaBiljkaBtn: Button
    private lateinit var pretragaET: EditText
    private lateinit var bojaSPIN: Spinner
    private lateinit var brzaPretraga: Button
    private var boje = listOf("red", "blue", "yellow", "orange", "purple","brown","green")
    private lateinit var bojeAdapter: ArrayAdapter<String>
    private var pretraga = false

    private lateinit var recyclerView: RecyclerView
    private lateinit var medicinskaListAdapter: MedicinskaListAdapter
    private lateinit var botanickaListAdapter: BotanickaListAdapter
    private lateinit var kuharskaListAdapter: KuharskaListAdapter

    private var trenutniAdapter: Int = 0
    private lateinit var biljkaDatabase: BiljkaDatabase
    //private var biljke = getListuBiljaka()
    private var biljke: List<Biljka>? = null

    val startActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            spinner.setSelection(0)
            trenutniAdapter = 1
            resetujBiljke()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        biljkaDatabase = BiljkaDatabase.getInstance(this)
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            biljke = biljkaDatabase.biljkaDao().getAllBiljkas()
        }

        resetBtn = findViewById(R.id.resetBtn)
        resetBtn.setOnClickListener {
            resetujBiljke()
        }

        spinner = findViewById(R.id.modSpinner)
        spinnerAdapter = ArrayAdapter<String>(this,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,modovi)

        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                odaberiMod(parentView.getItemAtPosition(position).toString())
            }

            override fun onNothingSelected(parentView: AdapterView<*>) { }
        }

        recyclerView = findViewById(R.id.biljkeRV)
        recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        novaBiljkaBtn = findViewById(R.id.novaBiljkaBtn)
        novaBiljkaBtn.setOnClickListener {
            showNovaBiljka()
        }

        pretragaET = findViewById(R.id.pretragaET)

        bojaSPIN = findViewById(R.id.bojaSPIN)
        bojeAdapter = ArrayAdapter<String>(this,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,boje)
        bojaSPIN.adapter = bojeAdapter

        brzaPretraga = findViewById(R.id.brzaPretraga)
        brzaPretraga.setOnClickListener {
            trazi()
        }

        //getUpcoming()
        //TrefleDAO.searchPoLatNazivu("Salvia hierosolymitana")
    }
    fun postaviMedicinskiMod(){
        medicinskaListAdapter = MedicinskaListAdapter(listOf())
        if(trenutniAdapter == 2 && !pretraga) {
            medicinskaListAdapter.updateBiljke(botanickaListAdapter.getBiljkeAdaptera())
        }
        else if(trenutniAdapter == 3){
            medicinskaListAdapter.updateBiljke(kuharskaListAdapter.getBiljkeAdaptera())
        }
        else{
            val scope = CoroutineScope(Job() + Dispatchers.Main)
            scope.launch {
                biljke = biljkaDatabase.biljkaDao().getAllBiljkas()
                medicinskaListAdapter.updateBiljke(biljke!!)
            }

        }
        trenutniAdapter = 1
        recyclerView.adapter = medicinskaListAdapter

        bojaSPIN.visibility = View.GONE
        pretragaET.visibility = View.GONE
        brzaPretraga.visibility = View.GONE

    }

    fun postaviBotanickiMod(){
        botanickaListAdapter = BotanickaListAdapter(listOf(),true)
        if(trenutniAdapter == 1) {
            botanickaListAdapter.updateBiljke(medicinskaListAdapter.getBiljkeAdaptera())
        }
        else if(trenutniAdapter == 3){
            botanickaListAdapter.updateBiljke(kuharskaListAdapter.getBiljkeAdaptera())
        }
        else{
            val scope = CoroutineScope(Job() + Dispatchers.Main)
            scope.launch {
                biljke = biljkaDatabase.biljkaDao().getAllBiljkas()
                botanickaListAdapter.updateBiljke(biljke!!)
            }

        }
        trenutniAdapter = 2
        recyclerView.adapter = botanickaListAdapter

        bojaSPIN.visibility = View.VISIBLE
        pretragaET.visibility = View.VISIBLE
        brzaPretraga.visibility = View.VISIBLE

    }

    fun postaviKuharskiMod(){
        kuharskaListAdapter = KuharskaListAdapter(listOf())
        if(trenutniAdapter == 2 && !pretraga) {
            kuharskaListAdapter.updateBiljke(botanickaListAdapter.getBiljkeAdaptera())
        }
        else if(trenutniAdapter == 1){
            kuharskaListAdapter.updateBiljke(medicinskaListAdapter.getBiljkeAdaptera())
        }
        else{
            val scope = CoroutineScope(Job() + Dispatchers.Main)
            scope.launch {
                biljke = biljkaDatabase.biljkaDao().getAllBiljkas()
                kuharskaListAdapter.updateBiljke(biljke!!)
            }

        }
        trenutniAdapter = 3
        recyclerView.adapter = kuharskaListAdapter

        bojaSPIN.visibility = View.GONE
        pretragaET.visibility = View.GONE
        brzaPretraga.visibility = View.GONE
    }

    fun odaberiMod(mod: String){
        if(mod == "Medicinski mod"){
            postaviMedicinskiMod()
        }else if(mod == "Botanički mod"){
            postaviBotanickiMod()
        }else if(mod == "Kuharski mod"){
            postaviKuharskiMod()
        }
    }
    fun resetujBiljke(){
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            biljke = biljkaDatabase.biljkaDao().getAllBiljkas()
            if(trenutniAdapter == 1){
                medicinskaListAdapter.updateBiljke(biljke!!)
            }
            else if(trenutniAdapter == 2){
                botanickaListAdapter.updateBiljke(biljke!!)
            }
            else if(trenutniAdapter==3){
                kuharskaListAdapter.updateBiljke(biljke!!)
            }
        }

    }
    private fun trazi(){
        if(pretragaET.text.isNotEmpty()){
            botanickaListAdapter = BotanickaListAdapter(listOf(),false)
            var trefleDAO = TrefleDAO()
            val scope = CoroutineScope(Job() + Dispatchers.Main)
            pretraga = true
            // Create a new coroutine on the UI thread
            scope.launch {
                botanickaListAdapter.updateBiljke(trefleDAO.getPlantsWithFlowerColor(bojaSPIN.selectedItem.toString(),pretragaET.text.toString()))
                recyclerView.adapter = botanickaListAdapter
            }


        }
    }
    private fun showNovaBiljka() {
        val intent = Intent(this, NovaBiljkaActivity::class.java)
        startActivityLauncher.launch(intent)
    }
}