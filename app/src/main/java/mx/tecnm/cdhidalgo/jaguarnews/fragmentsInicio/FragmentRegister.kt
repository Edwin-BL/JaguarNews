package mx.tecnm.cdhidalgo.jaguarnews.fragmentsInicio

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
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import mx.tecnm.cdhidalgo.jaguarnews.R
import mx.tecnm.cdhidalgo.jaguarnews.UsuarioDC
import mx.tecnm.cdhidalgo.jaguarnews.auth
import mx.tecnm.cdhidalgo.jaguarnews.baseDatos
import mx.tecnm.cdhidalgo.jaguarnews.dataClasses.Usuarios
import mx.tecnm.cdhidalgo.jaguarnews.storage
import java.io.ByteArrayOutputStream
import java.io.InputStream

class FragmentRegister : Fragment(R.layout.fragment_register) {
    private lateinit var foto: ImageView
    private lateinit var name: TextInputLayout
    private lateinit var lastName: TextInputLayout
    private lateinit var mLastName: TextInputLayout
    private lateinit var email: TextInputLayout
    private lateinit var pass: TextInputLayout

    private lateinit var rol: RadioGroup
    private val PICK_IMAGE_CAMERA = 1
    private val PICK_IMAGE_GALLERY = 2
    private lateinit var thisEmail: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        foto = view.findViewById(R.id.fotoUser)
        name = view.findViewById(R.id.rgName)
        lastName = view.findViewById(R.id.rgLastName)
        mLastName = view.findViewById(R.id.rgMLastName)
        email = view.findViewById(R.id.rgEmail)
        pass = view.findViewById(R.id.rgPass)

        rol = view.findViewById(R.id.rgRolUsuario)

        val btnRegistrar: MaterialButton = view.findViewById(R.id.btnRgRegister)
        val btnLogin: MaterialButton = view.findViewById(R.id.btnRgLogin)

        btnRegistrar.setOnClickListener {
            val thisName = name.editText?.text.toString()
            val thisLastName = lastName.editText?.text.toString()
            val thismLastName = mLastName.editText?.text.toString()

            thisEmail = email.editText?.text.toString()
            val thisPass = pass.editText?.text.toString()

            val confirmaDialog = AlertDialog.Builder(it.context)
            if (thisName.isEmpty()) {
                name.error = "Debes ingresar tu nombre"
            } else {
                name.error = null
                if (thisLastName.isEmpty()) {
                    lastName.error = "Debes ingresar tu apellido paterno"
                } else {
                    lastName.error = null
                    if (thismLastName.isEmpty()) {
                        mLastName.error = "Debes ingresar tu apellido materno"
                    } else {
                        mLastName.error = null
                        if (thisEmail.isEmpty()) {
                            email.error = "Debes ingresar un correo"
                        } else {
                            email.error = null
                            if (!thisEmail.endsWith("@itsch.edu.mx")) {
                                email.error = "Ingresa tu correo institucional"
                            } else{
                                email.error = null
                                if (thisPass.isEmpty()) {
                                    pass.error = "Ingresa una contraseña"
                                } else {
                                    if (thisPass.length < 6) {
                                        pass.error = "Ingresa al menos 6 caracteres"
                                    } else {
                                        val radioRol = view.findViewById<RadioButton>(rol.checkedRadioButtonId)
                                        val rolSeleccionado = radioRol.text
                                        if (rolSeleccionado == "Administrador"){
                                            val ventanaValidar: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                                            ventanaValidar.setTitle("Ingresa la clave")
                                            val claveAdmin = EditText(requireContext())
                                            ventanaValidar.setView(claveAdmin)
                                            ventanaValidar.setPositiveButton("Validar") { _, _ ->
                                                val key = claveAdmin.text.toString()
                                                if (key == "Admin23") {
                                                    confirmaDialog.setTitle("Confirmar Datos")
                                                    confirmaDialog.setMessage("""Usuario: ${name.editText?.text} ${lastName.editText?.text} ${mLastName.editText?.text}
                                                            Correo: ${email.editText?.text}
                                                            Contraseña: ${pass.editText?.text}
                                                            """.trimIndent())
                                                    confirmaDialog.setPositiveButton("Confirmar"){ confirmaDialog, wich ->
                                                        val numControl = "${(email.editText?.text)}".removeSuffix("@itsch.edu.mx")

                                                        UsuarioDC = Usuarios("fotosPerfil/${thisEmail}.jpg", "${thisName}", "${thisLastName}", "${thismLastName}", "${thisEmail}", "${numControl}", "administrador")

                                                        if (thisEmail.isNotEmpty() && thisPass.isNotEmpty()){
                                                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(thisEmail, thisPass).addOnCompleteListener {
                                                                if (it.isSuccessful){
                                                                    baseDatos.collection("users").document(thisEmail).set(UsuarioDC!!)
                                                                    findNavController().navigate(R.id.action_fragmentRegister_to_fragmentLogin)
                                                                    auth.signOut()
                                                                }else{
                                                                    showAlert()
                                                                }
                                                            }
                                                        }

                                                    }
                                                    confirmaDialog.setNegativeButton("Cancelar"){ confirmaDialog, wich ->
                                                        confirmaDialog.cancel()
                                                    }
                                                    confirmaDialog.show()
                                                } else {
                                                    Toast.makeText(requireContext(), "Clave incorrecta", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            ventanaValidar.setNegativeButton("Cancelar") { _, _ ->
                                                Toast.makeText(requireContext(), "Selecciona el perfil de estudiante", Toast.LENGTH_SHORT).show()
                                            }
                                            ventanaValidar.show()
                                        } else {
                                            confirmaDialog.setTitle("Confirmar Datos")
                                            confirmaDialog.setMessage("""Usuario: ${name.editText?.text} ${lastName.editText?.text} ${mLastName.editText?.text}
                                                            Correo: ${email.editText?.text}
                                                            Contraseña: ${pass.editText?.text}
                                                            """.trimIndent())
                                            confirmaDialog.setPositiveButton("Confirmar"){ confirmaDialog, wich ->

                                                val numControl = "${(email.editText?.text)}".removeSuffix("@itsch.edu.mx")

                                                UsuarioDC = Usuarios("fotosPerfil/${thisEmail}.jpg", "${thisName}", "${thisLastName}", "${thismLastName}", "${thisEmail}", "${numControl}", "estudiante")

                                                if (thisEmail.isNotEmpty() && thisPass.isNotEmpty()){
                                                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(thisEmail, thisPass).addOnCompleteListener {
                                                        if (it.isSuccessful){
                                                            baseDatos.collection("users").document(thisEmail).set(UsuarioDC!!)
                                                            findNavController().navigate(R.id.action_fragmentRegister_to_fragmentLogin)
                                                            auth.signOut()
                                                        }else{
                                                            showAlert()
                                                        }
                                                    }
                                                }

                                            }
                                            confirmaDialog.setNegativeButton("Cancelar"){ confirmaDialog, wich ->
                                                confirmaDialog.cancel()
                                            }
                                            confirmaDialog.show()
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

        foto.setOnClickListener {
            if (email.isEmpty()) {
                email.error = "Debes ingresar un correo"
            } else {
                email.error = null
                thisEmail = email.editText?.text.toString()
                if (!thisEmail.endsWith("@itsch.edu.mx")) {
                    email.error = "Ingresa tu correo institucional"
                } else {
                    email.error = null
                    showImagePickerDialog()
                }
            }
        }

        btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentRegister_to_fragmentLogin)
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
    private fun showAlert() {
        val alerta = AlertDialog.Builder(requireContext())
        alerta.setTitle("Error")
        alerta.setMessage("Se ha producido un error en la autenticación!!")
        alerta.setPositiveButton("Aceptar", null)
        alerta.show()
    }
}