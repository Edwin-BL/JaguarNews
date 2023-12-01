package mx.tecnm.cdhidalgo.jaguarnews.fragmentsAdministrador

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import mx.tecnm.cdhidalgo.jaguarnews.R
import mx.tecnm.cdhidalgo.jaguarnews.auth
import mx.tecnm.cdhidalgo.jaguarnews.baseDatos
import mx.tecnm.cdhidalgo.jaguarnews.storage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Date

class FragmentAgregarNoticia : Fragment(R.layout.fragment_agregar_noticia) {
    private val PICK_IMAGE_CAMERA = 1
    private val PICK_IMAGE_GALLERY = 2
    private lateinit var titulo: TextInputLayout
    private lateinit var foto: ImageView
    private lateinit var descripcion: TextInputLayout
    private lateinit var categoria: RadioGroup

    private var imagenURL: String = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val agregar: MaterialButton = view.findViewById(R.id.btnAddNoticia)
        titulo = view.findViewById(R.id.addNombreNoticia)
        foto = view.findViewById(R.id.addFotoNoticia)
        descripcion = view.findViewById(R.id.addDescripcionNoticia)
        categoria = view.findViewById(R.id.addRadioCategoria)

        foto.setOnClickListener {
            showImagePickerDialog()
        }
        val categoriaObtenida = categoria.toString()
        agregar.setOnClickListener {
            val radioCategoria = view.findViewById<RadioButton>(categoria.checkedRadioButtonId)
            val cateSeleccionado = radioCategoria.text
            val fecha = Timestamp(Date())
            val nombreDocumento = generarNombreDocumento(cateSeleccionado.toString().lowercase(), requireContext())
            val noticia = hashMapOf<String, Any>()
            noticia["titulo"] = titulo.editText?.text.toString()
            noticia["autor"] = auth.currentUser?.email.toString()
            noticia["imagenURL"] = "noticias/${cateSeleccionado.toString().lowercase()}/${nombreDocumento}.jpg"
            noticia["descripcion"] = descripcion.editText?.text.toString()
            noticia["fecha"] = fecha
            noticia["categoria"] = cateSeleccionado.toString().lowercase()

            imagenURL = noticia.get("imagenURL").toString()
            Toast.makeText(requireContext(), imagenURL, Toast.LENGTH_SHORT).show()
            baseDatos.collection("noticias").document(generarNombreDocumento(cateSeleccionado.toString().lowercase(), requireContext())).set(noticia).addOnSuccessListener {
                Toast.makeText(requireContext(), "registrado con éxito", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.nav_general)
            }
        }
    }
    private fun showImagePickerDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("De dónde quieres obtener la imagen?")
        val options = arrayOf("Cámara", "Galería")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, PICK_IMAGE_CAMERA)
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_CAMERA -> handleCameraResult(data)
                PICK_IMAGE_GALLERY -> handleGalleryResult(data)
            }
        }
    }

    private fun handleCameraResult(data: Intent?) {
        val imageBitmap = data?.extras?.get("data") as Bitmap
        foto.setImageBitmap(imageBitmap)

        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()
        uploadImage(imageData)
    }

    private fun handleGalleryResult(data: Intent?) {
        val imageUri = data?.data
        foto.setImageURI(imageUri)

        val resolver = activity?.contentResolver
        val inputStream: InputStream? = imageUri?.let { resolver?.openInputStream(it) }
        val imageData = inputStream?.readBytes()

        imageData?.let { uploadImage(it) }
    }

    private fun uploadImage(imageData: ByteArray) {
        val fileRef = storage.reference.child("${imagenURL}.jpg")

        fileRef.putBytes(imageData)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->

                }
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error uploading image", exception)
            }
    }

    fun generarNombreDocumento(categoria: String, context: Context): String {
        val sharedPref = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        var count = 1

        // Obtener el contador actual almacenado en SharedPreferences
        val storedCount = sharedPref.getInt("contador_$categoria", 0)

        if (storedCount != 0) {
            // Si hay un contador almacenado, usarlo y actualizarlo
            count = storedCount + 1
        }

        // Almacenar el nuevo contador en SharedPreferences
        with (sharedPref.edit()) {
            putInt("contador_$categoria", count)
            apply()
        }
        return "${categoria}${count.toString().padStart(2, '0')}"
    }
}