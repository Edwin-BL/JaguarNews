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
import mx.tecnm.cdhidalgo.jaguarnews.dataClasses.Usuarios

class AdaptadorUsuarios (val listaUsuarios: ArrayList<Usuarios>): RecyclerView.Adapter<AdaptadorUsuarios.UsuariosViewHolder>() {
    var onUsuarioClick:((Usuarios)->Unit)? = null
    class UsuariosViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val foto:ImageView = itemView.findViewById(R.id.foto_usuario)
        val nombre:TextView = itemView.findViewById(R.id.nombre_usuario)
        val correo:TextView = itemView.findViewById(R.id.emailusuario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuariosViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.card_usuarios, parent, false)
        return UsuariosViewHolder(vista)
    }

    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    override fun onBindViewHolder(holder: UsuariosViewHolder, position: Int) {
        val usuario = listaUsuarios[position]
        val pathReference = Firebase.storage.reference.child("${usuario.photo}")
        pathReference.downloadUrl.addOnSuccessListener {
            Glide.with(holder.foto.context).load(it).circleCrop().into(holder.foto)
        }
        holder.nombre.text = "${usuario.name} ${usuario.lastName} ${usuario.mlastName}"
        holder.correo.text = usuario.email
        holder.itemView.setOnClickListener {
            onUsuarioClick?.invoke(usuario)
        }
    }
}