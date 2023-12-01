package mx.tecnm.cdhidalgo.jaguarnews.ui.tics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TicsViewModel : ViewModel() {

    val datos : MutableLiveData<String> = MutableLiveData<String>()

}