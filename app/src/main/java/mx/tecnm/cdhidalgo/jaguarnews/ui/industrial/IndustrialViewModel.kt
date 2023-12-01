package mx.tecnm.cdhidalgo.jaguarnews.ui.industrial

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IndustrialViewModel : ViewModel() {
    val datos : MutableLiveData<String> = MutableLiveData<String>()
}