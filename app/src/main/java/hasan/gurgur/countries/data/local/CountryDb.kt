package hasan.gurgur.countries.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import hasan.gurgur.countries.model.Data

@Database(entities = [Data::class], version =1)
abstract class CountryDb : RoomDatabase() {
    abstract fun countryDao() : CountryDao
}

