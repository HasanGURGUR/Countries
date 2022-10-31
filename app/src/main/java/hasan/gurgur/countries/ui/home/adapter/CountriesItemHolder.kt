package hasan.gurgur.countries.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import hasan.gurgur.countries.base.BaseViewHolder
import hasan.gurgur.countries.databinding.ItemCountriesListBinding
import hasan.gurgur.countries.model.Data

class CountriesItemHolder(
    parent: ViewGroup,
    inflater: LayoutInflater
) : BaseViewHolder<ItemCountriesListBinding>(
    binding = ItemCountriesListBinding.inflate(inflater, parent, false)
) {

    fun bind(
        name: Data,
        characterClickCallback: ((Data) -> Unit)? = null,
        favClickCallback : ((Data,Int) -> Unit)? = null
    ) {
        with(binding) {
            binding.countries = name
            binding.mainLayout.setOnClickListener {
                characterClickCallback?.invoke(name)
            }
            binding.saveIcon.setOnClickListener{
                favClickCallback?.invoke(name,bindingAdapterPosition)
            }
            executePendingBindings()
        }
    }


}
