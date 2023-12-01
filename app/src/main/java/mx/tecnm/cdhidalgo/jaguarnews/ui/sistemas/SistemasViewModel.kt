package mx.tecnm.cdhidalgo.jaguarnews.ui.sistemas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SistemasViewModel : ViewModel() {
    val datos : MutableLiveData<String> = MutableLiveData<String>()
}