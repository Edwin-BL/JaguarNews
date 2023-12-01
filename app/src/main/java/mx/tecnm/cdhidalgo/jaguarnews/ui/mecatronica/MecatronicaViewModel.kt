package mx.tecnm.cdhidalgo.jaguarnews.ui.mecatronica

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MecatronicaViewModel : ViewModel() {
    val datos : MutableLiveData<String> = MutableLiveData<String>()
}