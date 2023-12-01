package mx.tecnm.cdhidalgo.jaguarnews

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import mx.tecnm.cdhidalgo.jaguarnews.dataClasses.Noticias
import mx.tecnm.cdhidalgo.jaguarnews.dataClasses.Usuarios

val auth = Firebase.auth
val storage = Firebase.storage
val baseDatos = Firebase.firestore
var UsuarioDC: Usuarios? = null
var NoticiasDC: Noticias? = null