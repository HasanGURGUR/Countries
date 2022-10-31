package hasan.gurgur.countries.data.remote.api

import hasan.gurgur.countries.model.CountriesModel
import hasan.gurgur.countries.model.CountryDetailModel
import hasan.gurgur.countries.model.Data
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("geo/countries")
    fun getCountries(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Single<CountriesModel>

    @GET("geo/countries/{code}")
    fun getCountryDetail(
        @Path("code") code: String
    ): Single<CountryDetailModel>
}