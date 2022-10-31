package hasan.gurgur.countries.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Link(
    val href: String,
    val rel: String
): Parcelable