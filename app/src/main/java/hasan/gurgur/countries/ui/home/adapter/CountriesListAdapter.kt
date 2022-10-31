package hasan.gurgur.countries.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hasan.gurgur.countries.base.BaseListAdapter
import hasan.gurgur.countries.model.Data

class CountriesListAdapter(
    private val characterClickCallback: ((Data?) -> Unit)?,
    private val favClick: ((Data,Int) -> Unit)?
) : BaseListAdapter<Data>(
    itemsSame = { old, new -> old.name == new.name },
    contentsSame = { old, new -> old == new }
) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CountriesItemHolder -> {
                holder.bind(
                    name = getItem(position),
                    characterClickCallback = characterClickCallback,
                    favClick
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return CountriesItemHolder(parent, inflater)
    }

}