package mx.tecnm.cdhidalgo.jaguarnews.ui.noticias

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NoticiasViewModel : ViewModel() {
    val datos : MutableLiveData<String> = MutableLiveData<String>()
}