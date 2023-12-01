package mx.tecnm.cdhidalgo.jaguarnews.fragmentsAdministrador

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import mx.tecnm.cdhidalgo.jaguarnews.R
import mx.tecnm.cdhidalgo.jaguarnews.baseDatos
import mx.tecnm.cdhidalgo.jaguarnews.dataClasses.Noticias

class FragmentDetalleNoticia : Fragment(R.layout.fragment_detalle_noticia) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val modificar : MaterialButton = view.findViewById(R.id.btnDetalleModificarN)
        val eliminar : MaterialButton = view.findViewById(R.id.btnDetalleEliminarN)
        val noticia = arguments?.getParcelable<Noticias>("noticia")


        if (noticia != null){
            val titulo: TextView = view.findViewById(R.id.detalleTituloNoticia)
            val autor: TextView = view.findViewById(R.id.detalleAutorNoticia)
            val foto: ImageView = view.findViewById(R.id.detalleFotoNoticia)
            val descripcion: TextView = view.findViewById(R.id.detalleDescripcionNoticia)
            val fecha: TextView = view.findViewById(R.id.detalleFechaNoticia)


            val pathReference = Firebase.storage.reference.child("${noticia.photo}")
            titulo.text = "Título: ${noticia.titulo}"
            autor.text = "Autor: ${noticia.autor}"
            pathReference.downloadUrl.addOnSuccessListener {
                Glide.with(this).load(it).into(foto)
            }
            descripcion.text = "Descripcion: ${noticia.descripcion}"
            fecha.text = "Fecha y hora: ${noticia.fecha}"

            modificar.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("noticia", noticia)
                findNavController().navigate(R.id.action_fragmentDetalleNoticia_to_fragmentModificarNoticia, bundle)
            }
                /*                baseDatos.collection("noticias").document("${noticia.titulo.toString()}").update().addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Noticia eliminada con éxito", Toast.LENGTH_SHORT).show()
                                }.addOnFailureListener {
                                    Toast.makeText(requireContext(), "Error al eliminar la noticia", Toast.LENGTH_SHORT).show()
                                }*/

            eliminar.setOnClickListener {
                baseDatos.collection("noticias").document("${noticia.titulo.toString()}").delete().addOnSuccessListener {
                    Toast.makeText(requireContext(), "Noticia eliminada con éxito", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al eliminar la noticia", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}