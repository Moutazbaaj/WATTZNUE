package de.syntax.androidabschluss.data.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.data.datamodels.newsmodels.NewsArticle
import de.syntax.androidabschluss.databinding.ListItemFavArticleBinding
import de.syntax.androidabschluss.ui.favorites.FavoritesFragmentDirections

/**
 * Adapter for displaying favorite news articles in a RecyclerView.
 *
 * @property dataset List of [NewsArticle] objects to be displayed.
 * @property navController NavController for navigating to the details fragment when an item is clicked.
 */

class FavoNewsAdapter(
    private val dataset: List<NewsArticle>,
    private val navController: NavController
) : RecyclerView.Adapter<FavoNewsAdapter.ItemViewHolder>() {


    /**
     * ViewHolder class for the adapter.
     *
     * @param binding View binding for the list item layout.
     */

    inner class ItemViewHolder(val binding: ListItemFavArticleBinding) :
        RecyclerView.ViewHolder(binding.root)


    /**
     * Inflates the item view layout and creates a new ViewHolder instance.
     *
     * @param parent The ViewGroup into which the new View will be added.
     * @param viewType The type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ListItemFavArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
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
        val article = dataset[position]


        // Convert image URL to Uri
        val imageUri = article.imgUrl.toUri().buildUpon()?.scheme("https")?.build()

        // Bind data to ViewHolder
        holder.binding.tvTitle.text = article.titel

        holder.binding.tvDateDate.text = article.publishedAt

        holder.binding.ivImge.load(imageUri) {
            placeholder(android.R.drawable.ic_menu_crop)
            error(R.drawable.round_broken_image_25)
            transformations(RoundedCornersTransformation(30f))
        }

        // Set click listener for item
        holder.binding.btnLink.setOnClickListener {
            val action =
                FavoritesFragmentDirections.actionFavoritesFragmentToFavoritesDetailsFragment(
                    article.id
                )
            navController.navigate(action)
        }

    }


}