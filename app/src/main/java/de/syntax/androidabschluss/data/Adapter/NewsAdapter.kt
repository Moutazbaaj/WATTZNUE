package de.syntax.androidabschluss.data.Adapter

import de.syntax.androidabschluss.data.datamodels.newsmodels.Article
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.ListItemSearchResutltsBinding
import de.syntax.androidabschluss.ui.search.SearchFragmentDirections


/**
 * Adapter class for the RecyclerView used to display news articles.
 *
 * @property dataset List<de.syntax.androidabschluss.data.datamodels.newsmodels.Article> The list of articles to display.
 */

class NewsAdapter(
    private val dataset: List<Article>,
    private val navController: NavController
) : RecyclerView.Adapter<NewsAdapter.ItemViewHolder>() {

    /**
     * ViewHolder class for each item in the RecyclerView.
     * @property binding ListItemSearchResutltsBinding The binding object for the item layout.
     */

    inner class ItemViewHolder(val binding: ListItemSearchResutltsBinding) :
        RecyclerView.ViewHolder(binding.root)


    /**
     * Creates a new ViewHolder instance for the item view.
     *
     * @param parent ViewGroup The parent view group.
     * @param viewType Int The type of the view.
     * @return ItemViewHolder The new ViewHolder instance.
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.ItemViewHolder {
        val binding = ListItemSearchResutltsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }


    /**
     * Binds the data to the ViewHolder.
     *
     * @param holder ItemViewHolder The ViewHolder to bind the data to.
     * @param position Int The position of the item in the dataset.
     */


    override fun onBindViewHolder(holder: NewsAdapter.ItemViewHolder, position: Int) {
        val article = dataset[position]

        // Convert the URL to Uri for image loading
        val imageUri = article.urlToImage?.toUri()?.buildUpon()?.scheme("https")?.build()


        // Bind data to ViewHolder
        holder.binding.ivImge.load(imageUri) {
            placeholder(android.R.drawable.ic_menu_crop)
            error(R.drawable.round_broken_image_25)
            transformations(RoundedCornersTransformation(30f))
        }

        holder.binding.tvTitle.text = article.title
        holder.binding.tvDateDate.text = article.publishedAt


        // Set click listener for item
        holder.binding.btnLink.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToSearchDetailsFragment(article.title.toString())
            navController.navigate(action)
        }

    }

    /**
     * Gets the total number of items in the dataset.
     *
     * @return Int The total number of items.
     */

    override fun getItemCount(): Int {
        return dataset.size
    }
}