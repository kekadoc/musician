package com.kekadoc.project.musician.core.network.impl

import com.kekadoc.project.musician.core.network.api.NetworkError

class NetworkException(error: NetworkError) : RuntimeException(error.message)