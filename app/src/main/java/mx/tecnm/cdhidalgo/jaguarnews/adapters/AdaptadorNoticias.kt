package mx.tecnm.cdhidalgo.jaguarnews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import mx.tecnm.cdhidalgo.jaguarnews.R
import mx.tecnm.cdhidalgo.jaguarnews.dataClasses.Noticias
import java.text.SimpleDateFormat
import java.util.Date

class AdaptadorNoticias (val listaNoticias: ArrayList<Noticias>): RecyclerView.Adapter<AdaptadorNoticias.NoticiasViewHolder>() {
    var onNoticiaClick:((Noticias)->Unit)? = null
    class NoticiasViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val titulo:TextView = itemView.findViewById(R.id.tituloNoticia)
        val autor:TextView = itemView.findViewById(R.id.autorNoticia)
        val foto:ImageView = itemView.findViewById(R.id.fotoNoticia)
        val descripcion:TextView = itemView.findViewById(R.id.descripcionNoticia)
        val fecha:TextView = itemView.findViewById(R.id.fechaNoticia)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiasViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.card_noticias, parent, false)
        return NoticiasViewHolder(vista)
    }

    override fun getItemCount(): Int {
        return listaNoticias.size
    }

    override fun onBindViewHolder(holder: NoticiasViewHolder, position: Int) {
        val noticia = listaNoticias[position]
        holder.titulo.text = noticia.titulo
        holder.autor.text = noticia.autor
        val pathReference = Firebase.storage.reference.child("${noticia.photo}")
        pathReference.downloadUrl.addOnSuccessListener {
            Glide.with(holder.foto.context).load(it).into(holder.foto)
        }
        holder.descripcion.text = noticia.descripcion
        holder.fecha.text = noticia.fecha
        holder.itemView.setOnClickListener {
            onNoticiaClick?.invoke(noticia)
        }
    }
}