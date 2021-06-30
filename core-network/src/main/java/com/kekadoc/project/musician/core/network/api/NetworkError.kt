package com.kekadoc.project.musician.core.network.api

interface NetworkError {
    val code: Int
    val message: String?
}