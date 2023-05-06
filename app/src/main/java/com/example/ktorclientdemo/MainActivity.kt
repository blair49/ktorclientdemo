package com.example.ktorclientdemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeoutException

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    //Create Ktor client
    private val httpClient = HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        coroutineScope.launch {
            try {
                val posts =
                    httpClient.get<List<Post>> { url("https://jsonplaceholder.typicode.com/posts") }
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                recyclerView.adapter = PostAdapter(posts)
            } catch (e: ClientRequestException) {
                Log.d(TAG, "ClientRequestException ${e.message}")
            } catch (e: ServerResponseException) {
                Log.d(TAG, "ServerResponseException ${e.message}")
            } catch (e: TimeoutException) {
                Log.d(TAG, "TimeoutException ${e.message}")
            } catch (e: Exception) {
                Log.d(TAG, "Exception ${e.message}")
            } finally {
                httpClient.close()
            }
        }
    }
}