package hasan.gurgur.countries.data.remote.api

import hasan.gurgur.countries.di.AppModule.API_KEY
import hasan.gurgur.countries.model.CountriesModel
import hasan.gurgur.countries.model.CountryDetailModel
import hasan.gurgur.countries.model.Data
import io.reactivex.Single
import javax.inject.Inject

class CountryRepository @Inject constructor(val apiService: ApiService) {

    fun getCountries(offset: Int,limit: Int): Single<CountriesModel> {
        return apiService.getCountries(offset,limit)
    }

    fun getCountryDetail(code : String): Single<CountryDetailModel> {
        return apiService.getCountryDetail(code)
    }
}