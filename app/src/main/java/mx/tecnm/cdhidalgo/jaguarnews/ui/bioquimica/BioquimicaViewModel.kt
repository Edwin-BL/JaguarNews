package mx.tecnm.cdhidalgo.jaguarnews.ui.bioquimica

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BioquimicaViewModel : ViewModel() {
    val datos : MutableLiveData<String> = MutableLiveData<String>()
}