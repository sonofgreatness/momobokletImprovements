package com.example.momobooklet_by_sm.presentation.data.remote.dto


import com.google.gson.annotations.SerializedName

data class AuthTokenDto(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)