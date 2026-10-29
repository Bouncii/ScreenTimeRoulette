package fr.bounci.screentimeroulette.remote

import io.ktor.client.call.body
import io.ktor.client.request.get

object HealthApi {
    private const val BASE_URL = "https://str.cmi-info.fr/"

    private val client = HttpClientFactory.create()

    suspend fun fetchApiHealth(): RemoteHealthResponse = client.get (
        urlString = "$BASE_URL/health"
    ).body()
}