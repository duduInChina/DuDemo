package com.dudu.weather.bean

import com.dudu.network.bean.IResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceResponse(val places: MutableList<Place>, val status: String, val error: String = "") :
    IResult {

    @Serializable
    data class Place(
        val name: String,
        val location: Location,
        @SerialName("formatted_address") val address: String
    )

    @Serializable
    data class Location(val lng: String, val lat: String)

    override fun getErrorCode() = status

    override fun isSuccess() = "ok"

    override fun getErrorMessage() = error
}