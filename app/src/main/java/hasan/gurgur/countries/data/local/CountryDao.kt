package hasan.gurgur.countries.data.local

import androidx.room.*
import hasan.gurgur.countries.model.Data
import hasan.gurgur.countries.util.Constant.TABLE_NAME

@Dao
interface CountryDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE clickedFavItem LIKE 1")
    fun getAllFavs(): MutableList<Data>

    @Query("SELECT * FROM $TABLE_NAME WHERE code LIKE :code")
    fun getFav(code: String): Data

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFav(entity: Data)

    @Update
    fun updateFavs(entity: Data)

    @Delete
    fun deleteFavs(entity: Data)
}