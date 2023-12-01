package mx.tecnm.cdhidalgo.jaguarnews.dataClasses

import android.os.Parcel
import android.os.Parcelable
import androidx.core.net.toUri
import java.net.URI

data class Usuarios(
    var photo: String?,
    var name:String?,
    var lastName:String?,
    var mlastName:String?,
    var email:String?,
    var numControl:String?,
    var rol:String?
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }
    constructor() : this(null, null, null, null, null, null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(photo)
        parcel.writeString(name)
        parcel.writeString(lastName)
        parcel.writeString(mlastName)
        parcel.writeString(email)
        parcel.writeString(numControl)
        parcel.writeString(rol)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Usuarios> {
        override fun createFromParcel(parcel: Parcel): Usuarios {
            return Usuarios(parcel)
        }

        override fun newArray(size: Int): Array<Usuarios?> {
            return arrayOfNulls(size)
        }
    }

}
