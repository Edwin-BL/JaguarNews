package mx.tecnm.cdhidalgo.jaguarnews.ui.general

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GeneralViewModel : ViewModel() {
    val datos : MutableLiveData<String> = MutableLiveData<String>()
}