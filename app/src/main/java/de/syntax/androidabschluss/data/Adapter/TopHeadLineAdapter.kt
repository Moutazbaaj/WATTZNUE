package de.syntax.androidabschluss.data.Adapter

import de.syntax.androidabschluss.data.datamodels.newsmodels.Article
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.syntax.androidabschluss.databinding.ListItemTopNewsResutsBinding


/**
 * Adapter class for the RecyclerView used to display top headlines.
 *
 * @property dataset List<de.syntax.androidabschluss.data.datamodels.newsmodels.Article> The list of articles to display.
 * @property articleUrl Function1<String, Unit> The function to handle article URL clicks.
 */

class TopHeadLineAdapter(
    private val dataset: List<Article>,
    private val articleUrl: (String) -> Unit
) : RecyclerView.Adapter<TopHeadLineAdapter.ItemViewHolder>() {


    /**
     * ViewHolder class for each item in the RecyclerView.
     *
     * @property binding ListItemTopNewsResutsBinding The binding object for the item layout.
     */

    inner class ItemViewHolder(val binding: ListItemTopNewsResutsBinding) :
        RecyclerView.ViewHolder(binding.root)


    /**
     * Creates a new ViewHolder instance for the item view.
     *
     * @param parent ViewGroup The parent view group.
     * @param viewType Int The type of the view.
     * @return ItemViewHolder The new ViewHolder instance.
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ListItemTopNewsResutsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(binding)
    }


    /**
     * Gets the total number of items in the dataset.
     *
     * @return Int The total number of items.
     */

    override fun getItemCount(): Int {
        return dataset.size
    }


    /**
     * Binds the data to the ViewHolder.
     *
     * @param holder ItemViewHolder The ViewHolder to bind the data to.
     * @param position Int The position of the item in the dataset.
     */

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val article = dataset[position]


        // Bind data to ViewHolder
        holder.binding.tvTitle.text = article.title

        holder.binding.tvDateDate.text = article.publishedAt

        holder.binding.btnLink.setOnClickListener {
            article.url?.let { url ->
                // Invoke the callback function to handle URL click
                articleUrl(url)
            }
        }
    }
}