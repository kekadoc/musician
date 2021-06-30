package com.kekadoc.project.musician.core.network.api

class NetworkException(error: NetworkError) : RuntimeException(error.message)