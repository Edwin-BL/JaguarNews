package mx.tecnm.cdhidalgo.jaguarnews.dataClasses

import android.os.Parcel
import android.os.Parcelable

data class Noticias(
    var titulo: String?,
    var autor:String?,
    var photo:String?,
    var descripcion:String?,
    var fecha:String?,
    var categoria:String?
):Parcelable{
    constructor(parcel: Parcel) : this(
    parcel.readString(),
    parcel.readString(),
    parcel.readString(),
    parcel.readString(),
    parcel.readString(),
    parcel.readString()
    ) {
    }

    constructor() : this(null, null, null, null, null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(titulo)
        parcel.writeString(autor)
        parcel.writeString(photo)
        parcel.writeString(descripcion)
        parcel.writeString(fecha)
        parcel.writeString(categoria)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Noticias> {
        override fun createFromParcel(parcel: Parcel): Noticias {
            return Noticias(parcel)
        }

        override fun newArray(size: Int): Array<Noticias?> {
            return arrayOfNulls(size)
        }
    }

}
