package com.example.projekatspirale

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BotanickaListAdapter(private var biljke : List<Biljka>, private var filterable: Boolean) : RecyclerView.Adapter<BotanickaListAdapter.BotanickiViewHolder> (){
    lateinit var trefleDAO: TrefleDAO
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BotanickaListAdapter.BotanickiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.botanical_view,parent,false)
        trefleDAO = TrefleDAO()
        trefleDAO.setContext(parent.context)
        if(filterable) {
            view.setOnClickListener {
                ostaviBotanickiSlicne(view)
            }
        }
        return BotanickiViewHolder(view)
    }

    override fun getItemCount(): Int {
        return biljke.size
    }
    fun getBiljkeAdaptera() = biljke

    override fun onBindViewHolder(
        holder: BotanickaListAdapter.BotanickiViewHolder,
        position: Int
    ) {
        holder.nazivBiljke.text = ""
        holder.porodicaBiljke.text = ""
        holder.klimatskiTipBiljke.text = ""
        holder.zemljisniTipBiljke.text = ""
        holder.slikaBiljke.setImageResource(R.drawable.ic_launcher_foreground)

        holder.nazivBiljke.text = biljke[position].naziv
        holder.porodicaBiljke.text = biljke[position].porodica
        if(biljke[position].klimatskiTipovi.isNotEmpty())
            holder.klimatskiTipBiljke.text = biljke[position].klimatskiTipovi[0].opis
        if(biljke[position].zemljisniTipovi.isNotEmpty())
            holder.zemljisniTipBiljke.text = biljke[position].zemljisniTipovi[0].naziv
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Create a new coroutine on the UI thread
        scope.launch {
            holder.slikaBiljke.setImageBitmap(trefleDAO.getImage(biljke[position]))
        }
    }

    fun updateBiljke(biljke: List<Biljka>){
        this.biljke = biljke
        notifyDataSetChanged()
    }
    inner class BotanickiViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val slikaBiljke: ImageView = itemView.findViewById(R.id.slikaItem)
        val nazivBiljke: TextView = itemView.findViewById(R.id.nazivItem)
        val porodicaBiljke: TextView = itemView.findViewById(R.id.porodicaItem)
        val klimatskiTipBiljke: TextView = itemView.findViewById(R.id.klimatskiTipItem)
        val zemljisniTipBiljke: TextView = itemView.findViewById(R.id.zemljisniTipItem)
    }
    fun ostaviBotanickiSlicne(view: View){
        val nazivBiljkeTextView: TextView = view.findViewById(R.id.nazivItem)
        val nazivBiljke: String = nazivBiljkeTextView.text.toString()
        lateinit var odabrana: Biljka

        for(i in biljke){
            if(i.naziv == nazivBiljke){
                odabrana = i
                break
            }
        }

        biljke = biljke.filter {it.porodica == odabrana.porodica && imaKlimatskiZajednickih(it.klimatskiTipovi,odabrana.klimatskiTipovi) && imaZemljisniZajednickih(it.zemljisniTipovi,odabrana.zemljisniTipovi)}
        updateBiljke(biljke)
    }
    fun imaKlimatskiZajednickih(klima1: List<KlimatskiTip>, klima2: List<KlimatskiTip>): Boolean{
        for(i in klima1){
            for(j in klima2){
                if(i == j) return true
            }
        }
        return false
    }
    fun imaZemljisniZajednickih(zemljiste1: List<Zemljiste>, zemljiste2: List<Zemljiste>): Boolean{
        for(i in zemljiste1){
            for(j in zemljiste2){
                if(i == j) return true
            }
        }
        return false
    }
}