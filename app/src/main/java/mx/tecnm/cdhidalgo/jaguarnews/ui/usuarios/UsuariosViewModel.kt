package mx.tecnm.cdhidalgo.jaguarnews.ui.usuarios

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UsuariosViewModel : ViewModel() {
    val datos : MutableLiveData<String> = MutableLiveData<String>()
}