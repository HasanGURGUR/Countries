package hasan.gurgur.countries.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hasan.gurgur.countries.data.remote.api.CountryRepository
import hasan.gurgur.countries.model.CountriesModel
import hasan.gurgur.countries.model.CountryDetailModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class CountryViewModel @Inject constructor(
    private val repository: CountryRepository
) : ViewModel() {

    val upcomingCountriesModel = MutableLiveData<CountriesModel>()
    val detailResponse = MutableLiveData<CountryDetailModel>()
    var tooManyRequest = MutableLiveData<Boolean>()
    private val disposable: CompositeDisposable = CompositeDisposable()



    fun fetchDataFromRemoteApi(offset: Int, limit: Int) {
        disposable.add(
            repository.getCountries(offset, limit)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<CountriesModel>() {
                    override fun onSuccess(response: CountriesModel) {
                        upcomingCountriesModel.value = response
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
        )
    }


    fun fetchCountryDeteail(code: String) {
        disposable.add(
            repository.getCountryDetail(code)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<CountryDetailModel>() {
                    override fun onSuccess(response: CountryDetailModel) {
                        detailResponse.value = response
                        tooManyRequest.value = false
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        tooManyRequest.value = true

                    }
                })
        )
    }

} 