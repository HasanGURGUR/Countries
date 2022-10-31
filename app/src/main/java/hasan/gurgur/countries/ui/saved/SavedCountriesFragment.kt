package hasan.gurgur.countries.ui.saved

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import dagger.hilt.android.AndroidEntryPoint
import hasan.gurgur.countries.R
import hasan.gurgur.countries.data.local.CountryDb
import hasan.gurgur.countries.databinding.FragmentHomeBinding
import hasan.gurgur.countries.databinding.FragmentSavedCountriesBinding
import hasan.gurgur.countries.model.Data
import hasan.gurgur.countries.ui.home.adapter.CountriesListAdapter
import hasan.gurgur.countries.util.Constant

@AndroidEntryPoint
class SavedCountriesFragment : Fragment() {
    private var _binding: FragmentSavedCountriesBinding? = null
    private val binding get() = _binding!!

    var list = arrayListOf<Data>()
    private val taskDB: CountryDb by lazy {
        Room.databaseBuilder(requireContext(), CountryDb::class.java, Constant.NOTE_DATABASE)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    lateinit var countriesListAdapter: CountriesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedCountriesBinding.inflate(inflater, container, false)
        val view = binding.root
        initAdapter()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.title.text = "Countries"
        binding.toolbar.backBtn.visibility = View.GONE
        binding.toolbar.saveBtn.visibility = View.GONE

        checkSavedList()
        list = taskDB.countryDao().getAllFavs() as ArrayList<Data>
        countriesListAdapter.submitList(list)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            } else {
                taskDB.countryDao().insertFav(data)
            }

            list = taskDB.countryDao().getAllFavs() as ArrayList<Data>
            countriesListAdapter.submitList(list)
            countriesListAdapter.notifyDataSetChanged()
            checkSavedList()
        })
        binding.savedCountriesRec.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.savedCountriesRec.adapter = countriesListAdapter
        countriesListAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun checkSavedList() {
        if (taskDB.countryDao().getAllFavs().isEmpty()) {
            binding.tvEmptySavedCountryList.visibility = View.VISIBLE
        }
    }
}