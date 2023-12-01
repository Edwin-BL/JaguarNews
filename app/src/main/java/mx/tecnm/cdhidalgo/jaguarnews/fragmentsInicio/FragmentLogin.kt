package mx.tecnm.cdhidalgo.jaguarnews.fragmentsInicio

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import mx.tecnm.cdhidalgo.jaguarnews.MainActivity
import mx.tecnm.cdhidalgo.jaguarnews.R
import mx.tecnm.cdhidalgo.jaguarnews.UsuarioDC
import mx.tecnm.cdhidalgo.jaguarnews.auth
import mx.tecnm.cdhidalgo.jaguarnews.baseDatos
import mx.tecnm.cdhidalgo.jaguarnews.dataClasses.Usuarios
import mx.tecnm.cdhidalgo.jaguarnews.storage
import java.io.ByteArrayOutputStream

class FragmentLogin : Fragment(R.layout.fragment_login) {

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userExist = auth.currentUser
        if (userExist != null){
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        val email: TextInputLayout = view.findViewById(R.id.liEmail)
        val pass: TextInputLayout = view.findViewById(R.id.liPassword)
        val btnLogin: MaterialButton = view.findViewById(R.id.btn_liLogin)
        val btnRegister: MaterialButton = view.findViewById(R.id.btn_liregister)
        val btnGoogle: SignInButton = view.findViewById(R.id.btnGoogle)


        btnLogin.setOnClickListener {
            val thisEmail = email.editText?.text.toString()
            val thisPass = pass.editText?.text.toString()

            if (thisEmail.isEmpty()) {
                email.error = "Debes ingresar un correo"
                if (!thisEmail.endsWith("@itsch.edu.mx")){
                    email.error = "Ingresa el correo institucional"
                }
            } else {
                email.error = null
                if (thisPass.isEmpty()) {
                    pass.error = "Debes ingresar una contraseña"
                } else {
                    pass.error = null
                    auth.signInWithEmailAndPassword(thisEmail, thisPass).addOnCompleteListener {
                        if (it.isSuccessful){
                            baseDatos.collection("users").whereEqualTo("email", thisEmail).get().addOnSuccessListener { documents ->
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                startActivity(intent)
                            }
                        }else{
                            notificacion()
                        }
                    }
                }
            }
        }
        btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentLogin_to_fragmentRegister)
        }
        // [START config_signin]
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        btnGoogle.setOnClickListener {
            signInGoogle()
        }
        // [END config_signin]

    }

    // [START signin]
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleRsults(task)
        }
    }
    private fun handleRsults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateUI(account: GoogleSignInAccount) {
        if (account.email!!.endsWith("@itsch.edu.mx")){
            baseDatos.collection("users").document("${account.email}").get().addOnSuccessListener { document ->
                val data = document.data
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                if (account.email.toString() == data?.get("email").toString()){
                    auth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful){
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(requireContext(), "Iniciaste sesion con exito", Toast.LENGTH_SHORT).show()
                            activity?.finish()
                        } else {
                            Toast.makeText(requireContext(), it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    val email = account.email.toString()
                    val name = account.displayName?.split(" ")
                    val numControl = email?.removeSuffix("@itsch.edu.mx")
                    val user = hashMapOf<String, String>()
                    user["photo"] = "fotosPerfil/${email}.jpg"
                    user["email"] = email
                    user["numControl"] = numControl.toString()
                    user["rol"] = "Estudiante"
                    if (name?.size == 4){
                        user["name"] = "${name?.get(0)} ${name?.get(1)}"
                        user["lastName"] = name?.get(2).toString()
                        user["mlastName"] = name?.get(3).toString()
                    }else{
                        user["name"] = "${name?.get(0)}"
                        user["lastName"] = name?.get(1).toString()
                        user["mlastName"] = name?.get(2).toString()
                    }

                    baseDatos.collection("users").document(email).set(user).addOnSuccessListener {
                        val fotoUsuario: Uri? = account?.photoUrl
                        Glide.with(this)
                            .asBitmap()
                            .load(fotoUsuario)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    guardarFotoStorage(resource, account)
                                }
                                override fun onLoadCleared(placeholder: Drawable?) {
                                }
                            })
                    }
                    auth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful){
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        } else {
                            Toast.makeText(requireContext(), it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }else{
            Toast.makeText(requireContext(), "Intenta con el correo institucional", Toast.LENGTH_SHORT).show()
        }
    }

    private fun notificacion() {
        val alerta = AlertDialog.Builder(requireContext())
        alerta.setTitle("Error")
        alerta.setMessage("Se ha producido un error en la autenticación del Usuario!!")
        alerta.setPositiveButton("Aceptar", null)
        alerta.show()
    }

    private fun signOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
            // Acción después del cierre de sesión
        }
    }

    private fun guardarFotoStorage(bitmap: Bitmap, account: GoogleSignInAccount) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val imagesRef = storage.reference.child("fotosPerfil/${account.email}.jpg")
        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Manejar el caso de fallo
        }.addOnSuccessListener {
            // Manejar el caso de éxito
        }
    }
}