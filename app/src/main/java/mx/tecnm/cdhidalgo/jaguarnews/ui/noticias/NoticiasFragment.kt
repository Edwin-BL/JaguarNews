package mx.tecnm.cdhidalgo.jaguarnews.ui.noticias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.tecnm.cdhidalgo.jaguarnews.R
import mx.tecnm.cdhidalgo.jaguarnews.adapters.AdaptadorNoticias
import mx.tecnm.cdhidalgo.jaguarnews.baseDatos
import mx.tecnm.cdhidalgo.jaguarnews.dataClasses.Noticias
import mx.tecnm.cdhidalgo.jaguarnews.databinding.FragmentNoticiasBinding
import java.text.SimpleDateFormat
import java.util.Date

class NoticiasFragment : Fragment() {

    private var _binding: FragmentNoticiasBinding? = null
    private lateinit var listaNoticias: ArrayList<Noticias>
    private lateinit var adaptadorNoticias: AdaptadorNoticias

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val noticiasViewModel =
            ViewModelProvider(this).get(NoticiasViewModel::class.java)

        _binding = FragmentNoticiasBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerViewNoticias: RecyclerView = binding.rvNoticias

        recyclerViewNoticias.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        listaNoticias = ArrayList()

        baseDatos.collection("noticias").get().addOnSuccessListener { documents ->
            for (document in documents){
                val seconds = document?.getTimestamp("fecha")!!.seconds
                val nanoseconds = document?.getTimestamp("fecha")!!.nanoseconds
                val timestamp = seconds * 1000 + nanoseconds / 1000000
                val date = Date(timestamp)
                val formato = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                listaNoticias.add(
                    Noticias(
                        "${document?.getString("titulo")}",
                        "${document?.getString("autor")}",
                        "${document?.getString("imagenURL")}",
                        "${document?.getString("descripcion")}",
                        "${formato.format(date)}",
                        "${document?.getString("categoria")}"
                    )
                )

            }
            //Adaptador Artesanias
            adaptadorNoticias = AdaptadorNoticias(listaNoticias)
            //AgregarProductos a ListaArtesanias
            recyclerViewNoticias.adapter = adaptadorNoticias
            adaptadorNoticias.onNoticiaClick = {
                val bundle = Bundle().apply {
                    putParcelable("noticia", it) // Asegúrate de que tu clase Noticias implementa Parcelable
                }
                findNavController().navigate(R.id.action_nav_noticias_to_fragmentDetalleNoticia, bundle)
            }
        }
        noticiasViewModel.datos.observe(viewLifecycleOwner) {
            adaptadorNoticias = AdaptadorNoticias(listaNoticias)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}