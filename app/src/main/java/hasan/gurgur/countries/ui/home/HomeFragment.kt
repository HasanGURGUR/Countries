package hasan.gurgur.countries.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import dagger.hilt.android.AndroidEntryPoint
import hasan.gurgur.countries.R
import hasan.gurgur.countries.databinding.FragmentHomeBinding
import hasan.gurgur.countries.data.local.CountryDb
import hasan.gurgur.countries.model.Data
import hasan.gurgur.countries.ui.home.adapter.CountriesListAdapter
import hasan.gurgur.countries.util.Constant.NOTE_DATABASE
import hasan.gurgur.countries.viewmodel.CountryViewModel

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    var offset = 0
    var limit = 10
    var loading = true
    var pastItemsVisible = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    private val taskDB: CountryDb by lazy {
        Room.databaseBuilder(requireContext(), CountryDb::class.java, NOTE_DATABASE)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    private val viewModel: CountryViewModel by viewModels()
    lateinit var countriesListAdapter: CountriesListAdapter
    var list = arrayListOf<Data>()


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        if (checkForInternet(requireContext())) {
            viewModel.fetchDataFromRemoteApi(offset, limit)
            initObserver()
            initAdapter()
        } else {
            Toast.makeText(
                requireContext(),
                "Please check internet connection!",
                Toast.LENGTH_SHORT
            ).show()
        }

        return view
    }

    private fun initObserver() {
        viewModel.upcomingCountriesModel.observe(requireActivity()) {
            list.addAll(it.data)
            countriesListAdapter.submitList(list)
            binding.progressBar.visibility = View.GONE
            checkSavedCountries()
            countriesListAdapter.notifyDataSetChanged()

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.title.text = "Countries"
        binding.toolbar.backBtn.visibility = View.GONE
        binding.toolbar.saveBtn.visibility = View.GONE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun checkSavedCountries() {
        val roomList = taskDB.countryDao().getAllFavs() as ArrayList<Data>
        list.forEach { it.clickedFavItem = false }
        for (i in roomList.indices) {

            val countryItem = list.find { it.code == roomList[i].code }
            if (countryItem != null) {
                countryItem.clickedFavItem = true
            }
        }
        countriesListAdapter.submitList(list)
        countriesListAdapter.notifyDataSetChanged()

    }

    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initAdapter() {
        countriesListAdapter = CountriesListAdapter({

            findNavController().navigate(
                R.id.action_global_detailFragment,
                bundleOf("detailCountry" to it)
            )
        }, favClick = { data, position ->
            list.get(position).clickedFavItem = !list.get(position).clickedFavItem
            if (data.clickedFavItem) {
                taskDB.countryDao().deleteFavs(data)
            }
            countriesListAdapter.submitList(list)
            countriesListAdapter.notifyDataSetChanged()
            taskDB.countryDao().insertFav(data)
        })

        binding.countriesRec.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.countriesRec.adapter = countriesListAdapter
        countriesListAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY


        binding.countriesRec.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount =
                        (binding.countriesRec.layoutManager as LinearLayoutManager).childCount
                    totalItemCount =
                        (binding.countriesRec.layoutManager as LinearLayoutManager).itemCount
                    pastItemsVisible =
                        (binding.countriesRec.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastItemsVisible >= totalItemCount) {
                            loading = false

                            binding.progressBar.visibility = View.VISIBLE
                            offset += limit
                            viewModel.fetchDataFromRemoteApi(offset, limit)
                            loading = true
                        }
                    }
                }
            }
        })
    }
}