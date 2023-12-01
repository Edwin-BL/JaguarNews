package mx.tecnm.cdhidalgo.jaguarnews.fragmentsAdministrador

import android.content.Intent
import android.os.Bundle
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
import mx.tecnm.cdhidalgo.jaguarnews.dataClasses.Usuarios

class FragmentDetalleUsuario : Fragment(R.layout.fragment_detalle_usuario) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val modificar : MaterialButton = view.findViewById(R.id.btnDetalleModificarU)
        val eliminar : MaterialButton = view.findViewById(R.id.btnDetalleEliminarU)
        val usuario = arguments?.getParcelable<Usuarios>("usuario")

        if (usuario != null){
            val foto: ImageView = view.findViewById(R.id.detalleFotoUsuario)
            val nombre: TextView = view.findViewById(R.id.detalleNombreUsuario)
            val email: TextView = view.findViewById(R.id.detalleCorreoUsuario)
            val numControl: TextView = view.findViewById(R.id.detalleNumControl)
            val rol: TextView = view.findViewById(R.id.detalleRolUsuario)


            val pathReference = Firebase.storage.reference.child("${usuario.photo}")
            pathReference.downloadUrl.addOnSuccessListener {
                Glide.with(this).load(it).into(foto)
            }
            nombre.text = "Nombre: ${usuario.name} ${usuario.lastName} ${usuario.mlastName}"
            email.text = "Correo: ${usuario.email}"
            numControl.text = "Número de Control: ${usuario.numControl}"
            rol.text = "Rol: ${usuario.rol}"

            modificar.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("usuario", usuario)
                findNavController().navigate(R.id.action_fragmentDetalleUsuario_to_fragmentModificarUsuario, bundle)
            }

            eliminar.setOnClickListener {
                baseDatos.collection("users").document("${usuario.email.toString()}").delete().addOnSuccessListener {
                    findNavController().navigate(R.id.action_fragmentDetalleUsuario_to_nav_usuarios)
                    Toast.makeText(requireContext(), "Usuario eliminado con éxito", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al eliminar el usuario", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}