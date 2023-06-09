package com.example.unsplashpractice.domain

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.unsplashpractice.databinding.ListPhotosItemBinding
import com.example.unsplashpractice.db.Photo

class DBAdapter(
    private val onClick: (String) -> Unit
) : PagingDataAdapter<Photo, DBViewHolder>(diffCallback = DiffUtilCallbackDB()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DBViewHolder {
        return DBViewHolder(
            ListPhotosItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DBViewHolder, position: Int) {
        val item = getItem(position)

        Glide.with(holder.binding.root.context)
            .load(item?.photoUrl)
            .onlyRetrieveFromCache(true)
            .into(holder.binding.photoItem)
        Glide.with(holder.binding.root.context)
            .load(item?.userPhotoUrl)
            .onlyRetrieveFromCache(true)
            .circleCrop()
            .into(holder.binding.userPhoto)
        holder.binding.userName.text = item?.name
        holder.binding.userId.text = """@""" + item?.userName
        holder.binding.likesCount.text = item?.likes.toString()
        holder.binding.likeIsSet = item?.likeByMe
        holder.binding.root.setOnClickListener {
            if (item != null) {
                onClick(item.pid)
            }
        }
    }


}



class DiffUtilCallbackDB : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean =
        oldItem.pid == newItem.pid

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean = oldItem == newItem
}

class DBViewHolder(val binding: ListPhotosItemBinding): RecyclerView.ViewHolder(binding.root)
