package com.example.projekatspirale

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicinskaListAdapter(private var biljke : List<Biljka>) : RecyclerView.Adapter<MedicinskaListAdapter.MedicinskiViewHolder> (){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MedicinskaListAdapter.MedicinskiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.medical_view,parent,false)
        view.setOnClickListener {
            ostaviMedicinskiSlicne(view)
        }
        return MedicinskiViewHolder(view)
    }

    override fun getItemCount(): Int {
        return biljke.size
    }

    override fun onBindViewHolder(
        holder: MedicinskaListAdapter.MedicinskiViewHolder,
        position: Int
    ) {
        holder.nazivBiljke.text = biljke[position].naziv
        holder.upozorenjeBiljke.text = biljke[position].medicinskoUpozorenje
        for(i in biljke[position].medicinskeKoristi.indices){
            holder.koristiBiljke[i].text = biljke[position].medicinskeKoristi[i].opis
        }
    }

    fun getBiljkeAdaptera() = biljke

    fun updateBiljke(biljke: List<Biljka>){
        this.biljke = biljke
        notifyDataSetChanged()
    }
    inner class MedicinskiViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val slikaBiljke: ImageView = itemView.findViewById(R.id.slikaItem)
        val nazivBiljke: TextView = itemView.findViewById(R.id.nazivItem)
        val upozorenjeBiljke: TextView = itemView.findViewById(R.id.upozorenjeItem)
        val koristiBiljke: ArrayList<TextView> = arrayListOf<TextView>(itemView.findViewById(R.id.korist1Item),itemView.findViewById(R.id.korist2Item),itemView.findViewById(R.id.korist3Item))
    }
    fun ostaviMedicinskiSlicne(view: View){
        val nazivBiljkeTextView: TextView = view.findViewById(R.id.nazivItem)
        val nazivBiljke: String = nazivBiljkeTextView.text.toString()
        lateinit var odabrana: Biljka

        for(i in biljke){
            if(i.naziv == nazivBiljke){
                odabrana = i
                break
            }
        }

        biljke = biljke.filter { imaMedicinskiZajednickih(it.medicinskeKoristi, odabrana.medicinskeKoristi)}
        updateBiljke(biljke)
    }
    fun imaMedicinskiZajednickih(korist1: List<MedicinskaKorist>, korist2: List<MedicinskaKorist>): Boolean{
        for(i in korist1){
            for(j in korist2){
                if(i == j) return true
            }
        }
        return false
    }
}