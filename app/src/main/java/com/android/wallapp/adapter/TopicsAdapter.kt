package com.android.wallapp.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.content.LocusId
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.android.wallapp.R
import com.android.wallapp.databinding.ItemGalleryTagBinding
import com.android.wallapp.models.CategoryResponse


class TopicsAdapter( private val listener: OnItemClickListener) : PagingDataAdapter<CategoryResponse, TopicsAdapter.ViewHolder>(PhotoDiffCallback()) {
    var context: Context? = null

    inner class ViewHolder(val binding: ItemGalleryTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemGalleryTagBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        Log.e("TAG_onBindViewHolder", "onBindViewHolder: "+getItem(position) )
        holder.binding.apply {
            image.load(getItem(position)?.coverPhoto?.urls?.full){
                crossfade(true)
                crossfade(400)
                transformations(CircleCropTransformation())
                    .placeholder(R.drawable.ic_error_placeholder)
            }
            name.text=getItem(position)?.title
        }
        holder.itemView.setOnClickListener {
            listener.onItemTopicClick(getItem(position)?.id)
        }
    }

    interface OnItemClickListener {
        fun onItemTopicClick(id: String?)
    }

    private class PhotoDiffCallback : DiffUtil.ItemCallback<CategoryResponse>() {
        override fun areItemsTheSame(oldItem: CategoryResponse, newItem: CategoryResponse) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CategoryResponse, newItem: CategoryResponse) = oldItem == newItem
    }
}