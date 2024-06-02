package com.example.projekatspirale

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
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

    private lateinit var recyclerView: RecyclerView
    private lateinit var medicinskaListAdapter: MedicinskaListAdapter
    private lateinit var botanickaListAdapter: BotanickaListAdapter
    private lateinit var kuharskaListAdapter: KuharskaListAdapter

    private var trenutniAdapter: Int = 0

    private var biljke = getListuBiljaka()

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

        //getUpcoming()
        //TrefleDAO.searchPoLatNazivu("Salvia hierosolymitana")
    }
    fun postaviMedicinskiMod(){
        medicinskaListAdapter = MedicinskaListAdapter(listOf())
        if(trenutniAdapter == 2) {
            medicinskaListAdapter.updateBiljke(botanickaListAdapter.getBiljkeAdaptera())
        }
        else if(trenutniAdapter == 3){
            medicinskaListAdapter.updateBiljke(kuharskaListAdapter.getBiljkeAdaptera())
        }
        else{
            medicinskaListAdapter.updateBiljke(biljke)
        }
        trenutniAdapter = 1
        recyclerView.adapter = medicinskaListAdapter

    }

    fun postaviBotanickiMod(){
        botanickaListAdapter = BotanickaListAdapter(listOf())
        if(trenutniAdapter == 1) {
            botanickaListAdapter.updateBiljke(medicinskaListAdapter.getBiljkeAdaptera())
        }
        else if(trenutniAdapter == 3){
            botanickaListAdapter.updateBiljke(kuharskaListAdapter.getBiljkeAdaptera())
        }
        else{
            botanickaListAdapter.updateBiljke(biljke)
        }
        trenutniAdapter = 2
        recyclerView.adapter = botanickaListAdapter

    }

    fun postaviKuharskiMod(){
        kuharskaListAdapter = KuharskaListAdapter(listOf())
        if(trenutniAdapter == 2) {
            kuharskaListAdapter.updateBiljke(botanickaListAdapter.getBiljkeAdaptera())
        }
        else if(trenutniAdapter == 1){
            kuharskaListAdapter.updateBiljke(medicinskaListAdapter.getBiljkeAdaptera())
        }
        else{
            kuharskaListAdapter.updateBiljke(biljke)
        }
        trenutniAdapter = 3
        recyclerView.adapter = kuharskaListAdapter
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
        if(trenutniAdapter == 1){
            medicinskaListAdapter.updateBiljke(biljke)
        }
        else if(trenutniAdapter == 2){
            botanickaListAdapter.updateBiljke(biljke)
        }
        else if(trenutniAdapter==3){
            kuharskaListAdapter.updateBiljke(biljke)
        }
    }
    private fun showNovaBiljka() {
        val intent = Intent(this, NovaBiljkaActivity::class.java)
        startActivityLauncher.launch(intent)
    }

    fun getUpcoming( ){
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Create a new coroutine on the UI thread
        scope.launch{
            // Opcija 1
            val result = TrefleRepository.getAllPlants()
            // Display result of the network request to the user
            Log.e("moje",result.toString())
            when (result) {
                is GetTrefleResponse -> onSuccess(result.biljke)
                else-> onError()
            }
        }
    }
    fun onSuccess(biljke:List<BiljkaPomocna>){
        val toast = Toast.makeText(this, "Upcoming movies found", Toast.LENGTH_SHORT)
        toast.show()
        //recentMoviesAdapter.updateMovies(movies)
    }
    fun onError() {
        val toast = Toast.makeText(this, "Search error", Toast.LENGTH_SHORT)
        toast.show()
    }
}