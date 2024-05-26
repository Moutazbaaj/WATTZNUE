package de.syntax.androidabschluss.data.Adapter

import de.syntax.androidabschluss.data.datamodels.newsmodels.Source
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.syntax.androidabschluss.databinding.ListItemSourcesBinding


/**
 * Adapter for displaying news sources in a RecyclerView.
 *
 * @property dataset List of [Source] objects to be displayed.
 * @property articleUrl Callback function to handle URL click events.
 */
class SourcesAdapter(
    private val dataset: List<Source>,
    private val articleUrl: (String) -> Unit
) : RecyclerView.Adapter<SourcesAdapter.ItemViewHolder>() {


    /**
     * ViewHolder class for the adapter.
     *
     * @param binding View binding for the list item layout.
     */
    inner class ItemViewHolder(val binding: ListItemSourcesBinding):
            RecyclerView.ViewHolder(binding.root)


    /**
     * Inflates the item view layout and creates a new ViewHolder instance.
     *
     * @param parent The ViewGroup into which the new View will be added.
     * @param viewType The type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ListItemSourcesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(binding)
    }


    /**
     * Returns the total number of items in the dataset.
     *
     * @return The total number of items in the dataset.
     */
    override fun getItemCount(): Int {
        return dataset.size
    }


    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val source = dataset[position]

        // Bind data to ViewHolder
        holder.binding.tvTitle.text = source.name
        holder.binding.tvDescription.text = source.description

        // Set click listener for item
        holder.binding.btnLink.setOnClickListener {
            source.url?.let{ url ->
                // Invoke the callback function to handle URL click
                articleUrl(url)
            }
        }

    }


}