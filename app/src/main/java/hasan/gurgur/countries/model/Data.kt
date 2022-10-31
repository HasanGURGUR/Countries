package hasan.gurgur.countries.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hasan.gurgur.countries.util.Constant.TABLE_NAME
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = TABLE_NAME)
data class Data(
    @PrimaryKey val code: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "wikiDataId") val wikiDataId: String,
    @ColumnInfo(name = "clickedFavItem")  var clickedFavItem : Boolean = false
) : Parcelable