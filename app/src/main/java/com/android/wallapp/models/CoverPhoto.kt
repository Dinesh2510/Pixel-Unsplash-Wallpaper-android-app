package com.android.wallapp.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoverPhoto(
    @SerializedName("alt_description")
    val altDescription: String,
    @SerializedName("blur_hash")
    val blurHash: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("id")
    val id: String,

    @SerializedName("slug")
    val slug: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("urls")
    val urls: Urls,
    @SerializedName("width")
    val width: Int
) : Parcelable