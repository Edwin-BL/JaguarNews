package mx.tecnm.cdhidalgo.jaguarnews

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import mx.tecnm.cdhidalgo.jaguarnews.databinding.ActivityMainBinding
import mx.tecnm.cdhidalgo.jaguarnews.fragmentsAdministrador.FragmentAgregarNoticia
import mx.tecnm.cdhidalgo.jaguarnews.fragmentsAdministrador.FragmentAgregarUsuario

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var fotoUsuario: ImageView
    private lateinit var nombreUsuario: TextView
    private lateinit var correoUsuario: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!auth.currentUser?.email!!.endsWith("@itsch.edu.mx")){
            val intent = Intent(this, Inicio::class.java)
            startActivity(intent)
            auth.signOut()
            signOut()
            Toast.makeText(this, "Debes iniciar con tu correo institucional", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(this, "Iniciaste sesion con éxito", Toast.LENGTH_SHORT).show()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val user = auth.currentUser
        user?.let {
            val email = it.email
            val docRef = baseDatos.collection("users").document(email!!)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val campo = document.getString("rol")
                        if (campo == "Estudiante"){
                            binding.appBarMain.btnAgregar.isInvisible = true
                        }
                    } else {

                    }
                }
                .addOnFailureListener { exception ->
                }
        }
        binding.appBarMain.btnAgregarNoticia.setOnClickListener { view ->
            val agregarNoticia = FragmentAgregarNoticia()
            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, agregarNoticia).addToBackStack(null).commit()
        }

        binding.appBarMain.btnAgregarUsuario.setOnClickListener { view ->
            val agregarUsuario = FragmentAgregarUsuario()
            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, agregarUsuario).addToBackStack(null).commit()
        }

        binding.appBarMain.btnSignOut.setOnClickListener {
            val intent = Intent(this, Inicio::class.java)
            startActivity(intent)
            auth.signOut()
            signOut()
            finish()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navMenu = navView.menu
        val newsHide = navMenu.findItem(R.id.nav_noticias)
        val usersHide = navMenu.findItem(R.id.nav_usuarios)

        val encabezadoMenu = navView.getHeaderView(0)
        fotoUsuario = encabezadoMenu.findViewById(R.id.fotoUsuario)
        nombreUsuario = encabezadoMenu.findViewById(R.id.nombreUsuario)
        correoUsuario = encabezadoMenu.findViewById(R.id.correoUsuario)

        val userExist = auth.currentUser
        val foto = userExist?.photoUrl
        Glide.with(this).load(foto).circleCrop().into(fotoUsuario)
        if (userExist?.displayName != null ){
            nombreUsuario.text = userExist?.displayName
        }else{
            baseDatos.collection("users").document(userExist?.email.toString()).get().addOnSuccessListener { document ->
                if (document != null) {
                    val data = document.data
                    nombreUsuario.text = "${data?.get("name")} ${data?.get("lastName")} ${data?.get("mlastName")}"
                } else {
                    Toast.makeText(this, "No se pudo obtener tu nombre", Toast.LENGTH_LONG).show()
                }
            }

        }
        correoUsuario.text = userExist?.email

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_general, R.id.nav_bioquimica, R.id.nav_gestion, R.id.nav_industrial, R.id.nav_mecatronica, R.id.nav_nanotecnologia, R.id.nav_sistemas, R.id.nav_tics, R.id.nav_noticias, R.id.nav_usuarios, R.id.nav_noticias, R.id.nav_usuarios
            ), drawerLayout
        )
        baseDatos.collection("users").document(userExist?.email.toString()).get().addOnSuccessListener { document ->
            if (document != null) {
                val rol = document.getString("rol")
                if (rol?.lowercase() == "Administrador".toString().lowercase()) {
                    newsHide.isVisible = true
                    usersHide.isVisible = true
                } else {
                    newsHide.isVisible = false
                    usersHide.isVisible = false
                }
            } else {

            }
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun signOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut().addOnCompleteListener(this) {
            // Acción después del cierre de sesión
        }
    }

}