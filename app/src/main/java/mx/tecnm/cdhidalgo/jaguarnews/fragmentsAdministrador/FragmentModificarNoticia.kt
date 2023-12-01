package mx.tecnm.cdhidalgo.jaguarnews.fragmentsAdministrador

import android.content.ContentValues.TAG
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import mx.tecnm.cdhidalgo.jaguarnews.R
import mx.tecnm.cdhidalgo.jaguarnews.baseDatos
import mx.tecnm.cdhidalgo.jaguarnews.dataClasses.Noticias
import mx.tecnm.cdhidalgo.jaguarnews.storage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Date

class FragmentModificarNoticia : Fragment(R.layout.fragment_modificar_noticia) {
    private val PICK_IMAGE_CAMERA = 1
    private val PICK_IMAGE_GALLERY = 2
    private lateinit var titulo: TextInputLayout
    private lateinit var foto: ImageView
    private lateinit var descripcion: TextInputLayout
    private lateinit var categoria: RadioGroup

    private lateinit var imagenURL: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val modificar: MaterialButton = view.findViewById(R.id.btnMfNoticia)
        val noticia = arguments?.getParcelable<Noticias>("noticia")

        if (noticia != null) {
            titulo = view.findViewById(R.id.mfNombreNoticia)
            foto = view.findViewById(R.id.mfFotoNoticia)
            descripcion = view.findViewById(R.id.mfDescripcionNoticia)
            categoria = view.findViewById(R.id.mfRadioCategoria)

            val pathReference = Firebase.storage.reference.child("${noticia.photo}")
            titulo.editText?.setText(noticia.titulo)
            pathReference.downloadUrl.addOnSuccessListener {
                Glide.with(this).load(it).into(foto)
            }

            foto.setOnClickListener {
                showImagePickerDialog()
            }
            descripcion.editText?.setText(noticia.descripcion.toString())
            val categoriaObtenida = noticia.categoria.toString()

            for (i in 0 until categoria.childCount) {
                val radioButton = categoria.getChildAt(i) as RadioButton
                if (radioButton.text.toString() == categoriaObtenida) {
                    radioButton.isChecked = true
                    break
                }
            }

            modificar.setOnClickListener {
                val radioRol = view.findViewById<RadioButton>(categoria.checkedRadioButtonId)
                val rolSeleccionado = radioRol.text
                val fecha = Timestamp(Date())

                var docId = ""
                baseDatos.collection("tu_coleccion").get().addOnSuccessListener { result ->
                        for (document in result) {
                            docId = document.id
                            break
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w("TAG", "Error obteniendo documentos.", exception)
                    }
/*
// Usa el ID del documento para realizar una consulta
                db.collection("tu_coleccion").document(docId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                        } else {
                            Log.d("TAG", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("TAG", "get failed with ", exception)
                    }*/
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
        val fileRef = storage.reference.child("${categoria}/${categoria.toString().lowercase()}.jpg")

        fileRef.putBytes(imageData)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    imagenURL = uri.toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error uploading image", exception)
            }
    }
}