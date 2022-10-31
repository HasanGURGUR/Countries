package hasan.gurgur.countries.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Metadata(
    val currentOffset: Int,
    val totalCount: Int
) : Parcelable