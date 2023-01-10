package com.webengage.personalization.data.network

import java.io.Serializable

enum class RequestMethod(private val s: String) : Serializable {
    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

    override fun toString(): String {
        return s
    }
}