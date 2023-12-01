package mx.tecnm.cdhidalgo.jaguarnews.ui.gestion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GestionViewModel : ViewModel() {
    val datos : MutableLiveData<String> = MutableLiveData<String>()
}