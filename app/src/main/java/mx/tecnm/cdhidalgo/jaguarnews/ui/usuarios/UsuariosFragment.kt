package mx.tecnm.cdhidalgo.jaguarnews.ui.usuarios

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.tecnm.cdhidalgo.jaguarnews.R
import mx.tecnm.cdhidalgo.jaguarnews.UsuarioDC
import mx.tecnm.cdhidalgo.jaguarnews.adapters.AdaptadorUsuarios
import mx.tecnm.cdhidalgo.jaguarnews.baseDatos
import mx.tecnm.cdhidalgo.jaguarnews.dataClasses.Usuarios
import mx.tecnm.cdhidalgo.jaguarnews.databinding.FragmentNoticiasBinding
import mx.tecnm.cdhidalgo.jaguarnews.databinding.FragmentTicsBinding
import mx.tecnm.cdhidalgo.jaguarnews.databinding.FragmentUsuariosBinding
import mx.tecnm.cdhidalgo.jaguarnews.fragmentsAdministrador.FragmentDetalleUsuario

class UsuariosFragment : Fragment() {

    private var _binding: FragmentUsuariosBinding? = null
    private lateinit var listaUsuarios: ArrayList<Usuarios>
    private lateinit var adaptadorUsuarios: AdaptadorUsuarios

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val usuariosViewModel =
            ViewModelProvider(this).get(UsuariosViewModel::class.java)

        _binding = FragmentUsuariosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerViewUsuarios: RecyclerView = binding.rvUsuarios

        recyclerViewUsuarios.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        listaUsuarios = ArrayList()

        baseDatos.collection("users").get().addOnSuccessListener { documents ->
            for (document in documents){
                listaUsuarios.add(Usuarios(
                    "fotosPerfil/${document?.get("email")}.jpg",
                    "${document?.getString("name")}",
                    "${document.get("lastName")}",
                    "${document.get("mlastName")}",
                    "${document?.get("email")}",
                    "${document?.get("numControl")}",
                    "${document?.get("rol")}"
                ))
            }
            //Adaptador Artesanias
            adaptadorUsuarios = AdaptadorUsuarios(listaUsuarios)
            //AgregarProductos a ListaArtesanias
            recyclerViewUsuarios.adapter = adaptadorUsuarios
            adaptadorUsuarios.onUsuarioClick = {
                val bundle = Bundle().apply {
                    putParcelable("usuario", it) // Asegúrate de que tu clase Usuario implementa Parcelable
                }
                findNavController().navigate(R.id.action_nav_usuarios_to_fragmentDetalleUsuario, bundle)
            }
        }

        usuariosViewModel.datos.observe(viewLifecycleOwner) {
            adaptadorUsuarios = AdaptadorUsuarios(listaUsuarios)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}