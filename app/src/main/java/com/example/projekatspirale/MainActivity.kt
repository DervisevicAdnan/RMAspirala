package com.example.projekatspirale

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var spinner : Spinner
    private var modovi = listOf<String>("Medicinski mod", "Kuharski mod", "Botanički mod")
    private lateinit var spinnerAdapter : ArrayAdapter<String>
    private lateinit var resetBtn: Button

    private lateinit var recyclerView: RecyclerView
    private lateinit var medicinskaListAdapter: MedicinskaListAdapter
    private lateinit var botanickaListAdapter: BotanickaListAdapter
    private lateinit var kuharskaListAdapter: KuharskaListAdapter

    private var trenutniAdapter: Int = 0

    private var biljke = getListuBiljaka()

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
}