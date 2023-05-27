package com.android.wallapp.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryResponse(
    @SerializedName("cover_photo")
    val coverPhoto: CoverPhoto,
    @SerializedName("description")
    val description: String,
    @SerializedName("ends_at")
    val endsAt: String,
    @SerializedName("featured")
    val featured: Boolean,
    @SerializedName("id")
    val id: String,
    @SerializedName("published_at")
    val publishedAt: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("starts_at")
    val startsAt: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("total_photos")
    val totalPhotos: Int,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("visibility")
    val visibility: String
) : Parcelable