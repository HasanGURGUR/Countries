package hasan.gurgur.countries.ui.home.detail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import coil.decode.SvgDecoder
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import hasan.gurgur.countries.R
import hasan.gurgur.countries.data.local.CountryDb
import hasan.gurgur.countries.databinding.FragmentDetailBinding
import hasan.gurgur.countries.databinding.FragmentHomeBinding
import hasan.gurgur.countries.model.CountryDetailModel
import hasan.gurgur.countries.model.Data
import hasan.gurgur.countries.util.Constant
import hasan.gurgur.countries.viewmodel.CountryViewModel

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DetailFragmentArgs by navArgs()
    private val viewModel: CountryViewModel by viewModels()

    private lateinit var data: Data
    private val taskDB: CountryDb by lazy {
        Room.databaseBuilder(requireContext(), CountryDb::class.java, Constant.NOTE_DATABASE)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data = args.detailCountry
        checkFavStatus()
        binding.toolbar.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.saveBtn.setOnClickListener {
            if (data.clickedFavItem) {
                //delete
                data.clickedFavItem = false
                taskDB.countryDao().deleteFavs(data)
            } else {
                //insert
                data.clickedFavItem = true
                taskDB.countryDao().insertFav(data)
            }
            checkFavStatus()
        }

        viewModel.fetchCountryDeteail(code = args.detailCountry.code)


        initObserver()
    }

    private fun initObserver(){
        viewModel.detailResponse.observe(viewLifecycleOwner) {
            initDetailPage(it)
            navigateCountryPage(it)
        }

        viewModel.tooManyRequest.observe(viewLifecycleOwner) {
            if (it) {
                showErrorDialog()
            }
        }
    }

    private fun showErrorDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Too Many Request")
        builder.setMessage("429 too many request error")
        builder.setPositiveButton("Try Again") { dialog, id ->
            viewModel.fetchCountryDeteail(code = args.detailCountry.code)
            dialog.dismiss()
        }.show()
    }

    private fun initDetailPage(detail: CountryDetailModel) {
        binding.toolbar.title.text = detail.data.name
        binding.countryCode.text = detail.data.code

        binding.flagImage.load(detail.data.flagImageUri) {
            decoderFactory { result, options, _ -> SvgDecoder(result.source, options) }
        }
    }


    private fun navigateCountryPage(wikiData: CountryDetailModel) {
        val countryWikiCode = wikiData.data.wikiDataId
        binding.btnMoreInformation.setOnClickListener {
            val url = "https://www.wikidata.org/wiki/"
            val newUrl = "$url $countryWikiCode"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(newUrl)
            startActivity(i)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkFavStatus() {
        if (data.clickedFavItem) {
            binding.toolbar.saveBtn.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_star_selected)
        } else {
            binding.toolbar.saveBtn.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_star)
        }
    }

}