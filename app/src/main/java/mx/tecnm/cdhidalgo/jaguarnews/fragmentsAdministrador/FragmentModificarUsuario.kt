package mx.tecnm.cdhidalgo.jaguarnews.fragmentsAdministrador

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import mx.tecnm.cdhidalgo.jaguarnews.R
import mx.tecnm.cdhidalgo.jaguarnews.baseDatos
import mx.tecnm.cdhidalgo.jaguarnews.dataClasses.Usuarios
import mx.tecnm.cdhidalgo.jaguarnews.storage
import java.io.ByteArrayOutputStream
import java.io.InputStream


class FragmentModificarUsuario : Fragment(R.layout.fragment_modificar_usuario) {
    private val PICK_IMAGE_CAMERA = 1
    private val PICK_IMAGE_GALLERY = 2
    private lateinit var foto: ImageView
    private lateinit var name: TextInputLayout
    private lateinit var lastName: TextInputLayout
    private lateinit var mLastName: TextInputLayout
    private lateinit var thisEmail: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val modificar: MaterialButton = view.findViewById(R.id.btnMfUsuario)
        val usuario = arguments?.getParcelable<Usuarios>("usuario")

        if (usuario != null) {
            foto = view.findViewById(R.id.mfFotoUser)
            name = view.findViewById(R.id.mfNombre)
            lastName = view.findViewById(R.id.mfLastName)
            mLastName = view.findViewById(R.id.mfMLastName)

            val rol: RadioGroup = view.findViewById(R.id.mfRolUsuario)

            val pathReference = Firebase.storage.reference.child("${usuario.photo}")
            pathReference.downloadUrl.addOnSuccessListener {
                Glide.with(this).load(it).circleCrop().into(foto)
            }
            foto.setOnClickListener {
                thisEmail = usuario.email.toString()
                showImagePickerDialog()
            }
            name.editText?.setText(usuario.name.toString())
            lastName.editText?.setText(usuario.lastName.toString())
            mLastName.editText?.setText(usuario.mlastName.toString())
            val rolObtenido = usuario.rol.toString()

            for (i in 0 until rol.childCount) {
                val radioButton = rol.getChildAt(i) as RadioButton
                if (radioButton.text.toString().lowercase() == rolObtenido) {
                    radioButton.isChecked = true
                    break
                }
            }

            modificar.setOnClickListener {
                val radioRol = view.findViewById<RadioButton>(rol.checkedRadioButtonId)

                val confirmaDialog = AlertDialog.Builder(it.context)

                val rolSeleccionado = radioRol.text

                if (name.isEmpty()) {
                    name.error = "Debes ingresar tu nombre"
                } else {
                    name.error = null
                    if (lastName.isEmpty()) {
                        lastName.error = "Debes ingresar tu apellido paterno"
                    } else {
                        lastName.error = null
                        if (mLastName.isEmpty()) {
                            mLastName.error = "Debes ingresar tu apellido materno"
                        } else {
                            mLastName.error = null
                            thisEmail = usuario.email.toString()
                            val radioRol = view.findViewById<RadioButton>(rol.checkedRadioButtonId)
                            val rolSeleccionado = radioRol.text.toString()
                            if (rolSeleccionado.lowercase() == "administrador"){
                                val ventanaValidar: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                                ventanaValidar.setTitle("Ingresa la clave")
                                val claveAdmin = EditText(requireContext())
                                ventanaValidar.setView(claveAdmin)
                                ventanaValidar.setPositiveButton("Validar") { _, _ ->
                                    val key = claveAdmin.text.toString()
                                    if (key == "Admin23") {
                                        confirmaDialog.setTitle("Confirmar Datos")
                                        confirmaDialog.setMessage("""Usuario: ${name.editText?.text} ${lastName.editText?.text} ${mLastName.editText?.text}
                                                            Correo: ${usuario.email.toString()}
                                                            Rol: ${rolSeleccionado}
                                                            """.trimIndent())
                                        confirmaDialog.setPositiveButton("Confirmar"){ confirmaDialog, wich ->
                                            val numControl = "${(usuario.email.toString())}".removeSuffix("@itsch.edu.mx")
                                            val user = hashMapOf<String, Any>()
                                            user["photo"] = "fotosPerfil/${thisEmail}.jpg"
                                            user["email"] = thisEmail
                                            user["numControl"] = numControl
                                            user["rol"] = rolSeleccionado.toString().lowercase()
                                            user["name"] = name?.editText?.text.toString()
                                            user["lastName"] = lastName.editText?.text.toString()
                                            user["mlastName"] = mLastName.editText?.text.toString()
                                            baseDatos.collection("users").document(thisEmail).update(user).addOnSuccessListener {
                                                findNavController().navigate(R.id.action_fragmentModificarUsuario_to_nav_usuarios)
                                                Toast.makeText(requireContext(), "Usuario modificado con exito", Toast.LENGTH_SHORT).show()
                                            }.addOnFailureListener {
                                                Toast.makeText(requireContext(), "Error al modificar el usuario", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                    confirmaDialog.setNegativeButton("Cancelar"){ confirmaDialog, wich ->
                                        confirmaDialog.cancel()
                                    }
                                    confirmaDialog.show()
                                }
                                ventanaValidar.setNegativeButton("Cancelar") { _, _ ->
                                    Toast.makeText(requireContext(), "Selecciona el perfil de estudiante", Toast.LENGTH_SHORT).show()
                                }
                                ventanaValidar.show()
                            } else {
                                confirmaDialog.setTitle("Confirmar Datos")
                                confirmaDialog.setMessage("""Usuario: ${name.editText?.text} ${lastName.editText?.text} ${mLastName.editText?.text}
                                                            Correo: ${thisEmail}
                                                            Rol: ${rolSeleccionado}
                                                            """.trimIndent())
                                confirmaDialog.setPositiveButton("Confirmar"){ confirmaDialog, wich ->
                                    val numControl = "${thisEmail}".removeSuffix("@itsch.edu.mx")
                                    val user = hashMapOf<String, Any>()
                                    user["photo"] = "fotosPerfil/${thisEmail}.jpg"
                                    user["email"] = thisEmail
                                    user["numControl"] = numControl
                                    user["rol"] = rolSeleccionado.lowercase()
                                    user["name"] = name?.editText?.text.toString()
                                    user["lastName"] = lastName.editText?.text.toString()
                                    user["mlastName"] = mLastName.editText?.text.toString()
                                    baseDatos.collection("users").document(thisEmail).update(user).addOnSuccessListener {
                                        findNavController().navigate(R.id.action_fragmentModificarUsuario_to_nav_usuarios)
                                        Toast.makeText(requireContext(), "Usuario modificado con exito", Toast.LENGTH_SHORT).show()
                                    }.addOnFailureListener {
                                        Toast.makeText(requireContext(), "Error al modificar el usuario", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            confirmaDialog.setNegativeButton("Cancelar"){ confirmaDialog, wich ->
                                confirmaDialog.cancel()
                            }
                            confirmaDialog.show()                                    }
                    }
                }
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
        val fileRef = storage.reference.child("fotosPerfil/${thisEmail}.jpg")

        fileRef.putBytes(imageData)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val imagenURL = uri.toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error uploading image", exception)
            }
    }
}

