package com.hotelbooking.app.service

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit

/**
 * Server-Sent Events (SSE) client that connects to the backend's /api/sse endpoint.
 *
 * After calling [connect], the service opens a long-lived HTTP connection to the backend.
 * Every time an admin updates the database (create/update/delete hotel), the backend
 * pushes an SSE event; this service parses it and emits it through [events].
 *
 * Usage:
 * ```
 * // In ViewModel:
 * SSEService.events.launchIn(viewModelScope)
 * SSEService.connect()
 *
 * // React to real-time events:
 * SSEService.events.onEach { event ->
 *     when (event.event) {
 *         "hotel_created", "hotel_updated", "hotel_deleted" -> fetchHotels()
 *     }
 * }.launchIn(viewModelScope)
 * ```
 */
object SSEService {

    private const val TAG = "SSEService"
    private const val SSE_PATH = "api/sse"
    private const val BASE_URL = "http://10.0.2.2:3000" // matches RetrofitClient BASE_URL

    /**
     * Raw SSE data class emitted by the backend.
     */
    data class SSEEvent(
        val event: String,
        val payload: Map<String, Any?>?,
        val timestamp: String?
    )

    /**
     * Hot flow of received SSE events. Collect this to react to real-time updates.
     */
    private val _events = MutableSharedFlow<SSEEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<SSEEvent> = _events

    /**
     * Connection state exposed as a simple enum.
     */
    enum class ConnectionState { DISCONNECTED, CONNECTING, CONNECTED, ERROR }
    private val _connectionState = MutableSharedFlow<ConnectionState>(extraBufferCapacity = 1)
    val connectionState: SharedFlow<ConnectionState> = _connectionState

    private var eventSource: EventSource? = null
    private var scope: CoroutineScope? = null

    private val client by lazy {
        OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS) // keep-alive heartbeat
            .connectTimeout(0, TimeUnit.MILLISECONDS)
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()
    }

    private val request by lazy {
        Request.Builder()
            .url("$BASE_URL/$SSE_PATH")
            .header("Accept", "text/event-stream")
            .header("Cache-Control", "no-cache")
            .build()
    }

    private val listener = object : EventSourceListener() {

        override fun onOpen(eventSource: EventSource, response: Response) {
            Log.d(TAG, "SSE connected: ${response.code} ${response.message}")
            _connectionState.tryEmit(ConnectionState.CONNECTED)
        }

        override fun onEvent(
            eventSource: EventSource,
            id: String?,
            type: String?,
            data: String
        ) {
            Log.d(TAG, "SSE event received: type=$type, data=$data")
            parseAndEmit(data)
        }

        override fun onClosed(eventSource: EventSource) {
            Log.d(TAG, "SSE connection closed")
            _connectionState.tryEmit(ConnectionState.DISCONNECTED)
        }

        override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
            val msg = t?.message ?: response?.message ?: "unknown"
            Log.e(TAG, "SSE failure: $msg (response code: ${response?.code})")
            _connectionState.tryEmit(ConnectionState.ERROR)
            // OkHttp auto-reconnects by default; schedule a retry
            scheduleReconnect()
        }
    }

    /**
     * Establishes the SSE connection using the provided CoroutineScope.
     * Call this once when the app/screen becomes active.
     */
    fun connect(scope: CoroutineScope) {
        this.scope = scope
        if (eventSource != null) {
            Log.d(TAG, "connect() called but already connected; ignoring")
            return
        }

        scope.launch(Dispatchers.Main) {
            _connectionState.emit(ConnectionState.CONNECTING)
        }

        val factory = EventSources.createFactory(client)
        eventSource = factory.newEventSource(request, listener)
        Log.d(TAG, "SSE connecting to $BASE_URL/$SSE_PATH ...")
    }

    /**
     * Gracefully closes the SSE connection.
     * Call this when the app/screen goes to background or is disposed.
     */
    fun disconnect() {
        eventSource?.cancel()
        eventSource = null
        _connectionState.tryEmit(ConnectionState.DISCONNECTED)
        Log.d(TAG, "SSE disconnected")
    }

    private fun scheduleReconnect() {
        scope?.launch(Dispatchers.Main) {
            kotlinx.coroutines.delay(5_000)
            if (eventSource == null) {
                Log.d(TAG, "SSE attempting reconnect after failure...")
                connect(requireNotNull(scope))
            }
        }
    }

    /**
     * Parse SSE data line and emit to the events flow.
     * Expected format from backend: `data: {"event":"hotel_updated","payload":{"id":"2"},"timestamp":"..."}`
     */
    private fun parseAndEmit(raw: String) {
        val json = raw.trim()
        if (json.isEmpty() || json.startsWith(":")) return // ignore heartbeat/comments

        try {
            val jsonObject = com.google.gson.JsonParser.parseString(json).asJsonObject
            val event = jsonObject.get("event")?.asString ?: return
            val timestamp = jsonObject.get("timestamp")?.asString
            val payload = jsonObject.getAsJsonObject("payload")?.let { obj ->
                obj.entrySet().associate { (key, value) ->
                    key to when {
                        value.isJsonNull -> null
                        value.isJsonPrimitive -> value.asJsonPrimitive.let { p ->
                            when {
                                p.isNumber -> p.asNumber
                                p.isBoolean -> p.asBoolean
                                else -> p.asString
                            }
                        }
                        else -> value.toString()
                    }
                }
            }
            scope?.launch {
                _events.emit(SSEEvent(event, payload, timestamp))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse SSE data: `$json`", e)
        }
    }
}
