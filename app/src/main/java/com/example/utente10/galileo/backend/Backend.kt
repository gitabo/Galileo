package com.example.utente10.galileo.backend

import com.example.utente10.galileo.Application
import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import org.json.JSONObject

//request url
val ipAddress = "192.168.1.240"
val baseUrl = "http://${ipAddress}:8080/GalileoServer/"
val getDBVersion = "getDBVersion"
val getMacroareas = "getMacroareas"
val updateStatistics = "updateStatistics"



interface ResponseListener {
    fun onResponse(response: String)
}

interface ErrorListener {
    fun onError(error: String?)
}

fun sendRequest(app: Context, method: Int, url: String, json: JSONObject, responseListener: ResponseListener, errorListener: ErrorListener) {


    val jsonReq = JsonObjectRequest(method, url, json,
            Response.Listener<JSONObject> { response ->
                responseListener.onResponse(response.toString())
            },
            Response.ErrorListener { error ->
                var response: String? = null
                if (error?.networkResponse?.data != null) {
                    response = String(error.networkResponse.data)
                }
                errorListener.onError(response)
            })

    jsonReq.retryPolicy = DefaultRetryPolicy()
    (app as Application).requestQueue.add(jsonReq)
}

fun sendRequest(app: Context, method: Int, url: String, parameters: Map<String, String>, responseListener: ResponseListener, errorListener: ErrorListener) {

    val stringReq = object : StringRequest(method, url,
            Response.Listener<String> { response ->
                responseListener.onResponse(response)
            },
            Response.ErrorListener { error ->
                var response: String? = null
                if (error?.networkResponse?.data != null) {
                    response = String(error.networkResponse.data)
                }
                errorListener.onError(response)
            }) {
        override fun getParams(): Map<String, String> = parameters
    }

    stringReq.retryPolicy = DefaultRetryPolicy()
    (app as Application).requestQueue.add(stringReq)
}

fun getDBVersion(app: Context, responseListener: ResponseListener, errorListener: ErrorListener) {
    sendRequest(app, Request.Method.GET, baseUrl+ getDBVersion, JSONObject(), responseListener, errorListener)
}

fun getMacroareas(app: Context, responseListener: ResponseListener, errorListener: ErrorListener) {
    sendRequest(app, Request.Method.GET, baseUrl+ getMacroareas, JSONObject(), responseListener, errorListener)
}

fun sendStatistics(app: Context, labels: List<String>, responseListener: ResponseListener, errorListener: ErrorListener) {
    val visits = Visits(labels)
    val json = JSONObject(Gson().toJson(visits))
    sendRequest(app, Request.Method.POST, baseUrl + updateStatistics, json, responseListener, errorListener)
}

