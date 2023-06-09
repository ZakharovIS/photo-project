package com.example.unsplashpractice.domain

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.unsplashpractice.data.models.PreviewPhoto
import com.example.unsplashpractice.databinding.CollectionListPhotosItemBinding
import com.example.unsplashpractice.utils.CheckInternetConnection

class CollectionsPhotoAdapter(
    private val onClick: (String) -> Unit,
    private val onLikeClick: (PreviewPhoto) -> Unit
) : PagingDataAdapter<PreviewPhoto, CollectionPhotosViewHolder>(diffCallback = DiffUtilCallbackCollectionPhotos()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionPhotosViewHolder {
        return CollectionPhotosViewHolder(
            CollectionListPhotosItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CollectionPhotosViewHolder, position: Int) {
        val item = getItem(position)
        Glide.with(holder.binding.root.context)
            .load(item?.urls?.regular)
            .into(holder.binding.photoItem)
        Glide.with(holder.binding.root.context)
            .load(item?.user?.profile_image?.medium)
            .circleCrop()
            .into(holder.binding.userPhoto)
        holder.binding.userName.text = item?.user?.name
        holder.binding.userId.text = """@""" + item?.user?.username
        holder.binding.likesCount.text = item?.likes?.toString()
        holder.binding.likeIsSet = item?.liked_by_user

        holder.binding.like.setOnClickListener {
            if (item != null && CheckInternetConnection.isOnline(context = holder.binding.root.context)) {
                Log.d(
                    "Internet",
                    "${CheckInternetConnection.isOnline(context = holder.binding.root.context)}"
                )
                onLikeClick(item)
                if (item.liked_by_user!!) {
                    item.liked_by_user = false
                    item.likes = item.likes!! - 1
                } else {
                    item.liked_by_user = true
                    item.likes = item.likes!! + 1
                }
                holder.binding.likesCount.text = item.likes.toString()
                holder.binding.likeIsSet = item.liked_by_user
            }

        }

        holder.binding.root.setOnClickListener {
            if (item != null) {
                onClick(item.id!!)
            }
        }
    }


}



class DiffUtilCallbackCollectionPhotos : DiffUtil.ItemCallback<PreviewPhoto>() {
    override fun areItemsTheSame(oldItem: PreviewPhoto, newItem: PreviewPhoto): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: PreviewPhoto, newItem: PreviewPhoto): Boolean = oldItem.likes == newItem.likes
}

class CollectionPhotosViewHolder(val binding: CollectionListPhotosItemBinding): RecyclerView.ViewHolder(binding.root)
