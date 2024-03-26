package com.example.projekatspirale

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KuharskaListAdapter(private var biljke : List<Biljka>) : RecyclerView.Adapter<KuharskaListAdapter.KuharskaViewHolder> (){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KuharskaListAdapter.KuharskaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cooking_view,parent,false)
        view.setOnClickListener {
            ostaviKuharskiSlicne(view)
        }
        return KuharskaViewHolder(view)
    }

    override fun getItemCount(): Int {
        return biljke.size
    }

    fun getBiljkeAdaptera() = biljke

    override fun onBindViewHolder(
        holder: KuharskaListAdapter.KuharskaViewHolder,
        position: Int
    ) {
        holder.nazivBiljke.text = biljke[position].naziv
        holder.profilOkusaBiljke.text = biljke[position].profilOkusa.opis
        for(i in biljke[position].jela.indices){
            holder.jelaBiljke[i].text = biljke[position].jela[i]
        }
    }

    fun updateBiljke(biljke: List<Biljka>){
        this.biljke = biljke
        notifyDataSetChanged()
    }
    inner class KuharskaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val slikaBiljke: ImageView = itemView.findViewById(R.id.slikaItem)
        val nazivBiljke: TextView = itemView.findViewById(R.id.nazivItem)
        val profilOkusaBiljke: TextView = itemView.findViewById(R.id.profilOkusaItem)
        val jelaBiljke: ArrayList<TextView> = arrayListOf<TextView>(itemView.findViewById(R.id.jelo1Item),itemView.findViewById(R.id.jelo2Item),itemView.findViewById(R.id.jelo3Item))
    }

    fun ostaviKuharskiSlicne(view: View){
        val nazivBiljkeTextView: TextView = view.findViewById(R.id.nazivItem)
        val nazivBiljke: String = nazivBiljkeTextView.text.toString()
        lateinit var odabrana: Biljka

        for(i in biljke){
            if(i.naziv == nazivBiljke){
                odabrana = i
                break
            }
        }

        biljke = biljke.filter { imaKuharskiZajednickih(it.jela, odabrana.jela) || it.profilOkusa == odabrana.profilOkusa}
        updateBiljke(biljke)
    }
    fun imaKuharskiZajednickih(jela1: List<String>, jela2: List<String>): Boolean{
        for(i in jela1){
            for(j in jela2){
                if(i == j) return true
            }
        }
        return false
    }
}