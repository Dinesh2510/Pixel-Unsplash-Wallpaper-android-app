package com.android.wallapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.android.wallapp.R
import com.android.wallapp.databinding.ItemPhotoBinding
import com.android.wallapp.models.PhotoResponse


class PhotoAdapter(
    private val listener: OnItemClickListener
) : PagingDataAdapter<PhotoResponse, PhotoAdapter.GalleryViewHolder>(PhotoDiffCallback()) {

    inner class GalleryViewHolder(
        private val binding: ItemPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
//            binding.parentItemPhotoConstraint.setOnClickListener {
//                val position = bindingAdapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    val item = getItem(position)
////                    if (item != null)
//                }
//            }
        }

        fun bind(photo: PhotoResponse) = with(binding) {
            itemImageView.load(photo.urls.full) {
                crossfade(600)
                placeholder(R.drawable.ic_error_placeholder)
                error(R.drawable.ic_error_placeholder)
            }
            setImageDimensionRatio(binding, calculateImageDimensionRatio(photo))
            itemImageView.setOnClickListener {
                listener.onPhotoClick(photo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val photo = getItem(position)
        if (photo != null) {
            holder.bind(photo)
        }
    }

    interface OnItemClickListener {
        fun onPhotoClick(photo: PhotoResponse)
    }

    private class PhotoDiffCallback : DiffUtil.ItemCallback<PhotoResponse>() {
        override fun areItemsTheSame(oldItem: PhotoResponse, newItem: PhotoResponse) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PhotoResponse, newItem: PhotoResponse) = oldItem == newItem
    }

    private fun calculateImageDimensionRatio(photo: PhotoResponse): String {
        return if (photo.width.toFloat() / photo.height.toFloat() > 1.8) {
            String.format("4000:3000")
        } else {
            String.format("%d:%d", photo.width, photo.height)
        }
    }

    private fun setImageDimensionRatio(binding: ItemPhotoBinding, ratio: String) {
        val set = ConstraintSet()
        binding.apply {
            set.clone(parentItemPhotoConstraint)
            set.setDimensionRatio(itemImageView.id, ratio)
            set.applyTo(parentItemPhotoConstraint)
        }
    }
}