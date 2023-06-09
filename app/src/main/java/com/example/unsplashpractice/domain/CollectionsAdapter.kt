package com.example.unsplashpractice.domain

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.unsplashpractice.data.models.UnsplashCollection
import com.example.unsplashpractice.databinding.CollectionItemBinding

class CollectionsAdapter(
    private val onClick: (String) -> Unit
) : PagingDataAdapter<UnsplashCollection, CollectionViewHolder>(diffCallback = DiffUtilCallbackCollection()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        return CollectionViewHolder(
            CollectionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val item = getItem(position)
        Glide.with(holder.binding.root.context)
            .load(item?.cover_photo?.urls?.regular)
            .into(holder.binding.collectionCoverView)
        Glide.with(holder.binding.root.context)
            .load(item?.user?.profile_image?.medium)
            .circleCrop()
            .into(holder.binding.userPhoto)
        holder.binding.userName.text = item?.user?.name
        holder.binding.userId.text = """@""" + item?.user?.username
        holder.binding.collectionTitle.text = item?.title.toString()
        holder.binding.photosCountView.text = item?.total_photos.toString() + " фотографий"

        holder.binding.root.setOnClickListener {
            if (item != null) {
                onClick(item.id!!)
            }
        }
    }


}



class DiffUtilCallbackCollection : DiffUtil.ItemCallback<UnsplashCollection>() {
    override fun areItemsTheSame(oldItem: UnsplashCollection, newItem: UnsplashCollection): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: UnsplashCollection, newItem: UnsplashCollection): Boolean = oldItem == newItem
}

class CollectionViewHolder(val binding: CollectionItemBinding): RecyclerView.ViewHolder(binding.root)
