package hasan.gurgur.countries.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CountriesModel(
    val `data`: List<Data>,
    val links: List<Link>,
    val metadata: Metadata
) : Parcelable