package com.example.utente10.galileo.backend


import com.example.utente10.galileo.Application
import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.utente10.galileo.bean.Landmark
import com.example.utente10.galileo.example.macroareasList
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.realm.RealmResults
import org.json.JSONArray
import org.json.JSONObject

//request url
val ipAddress = "http://192.168.1.227:8080/"
val baseUrl = "${ipAddress}GalileoServer/"
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

fun getMacroareas(app: Context, responseListener: ResponseListener, errorListener: ErrorListener) {

    //val map = HashMap<String, String>()
    //sendRequest(app, Request.Method.GET, macroareaUrl, map, responseListener, errorListener)
    responseListener.onResponse(macroareasList)
}

fun sendStatistics(app: Context, labels: ArrayList<String>, responseListener: ResponseListener, errorListener: ErrorListener) {
    val visits = Visits(labels)
    val json = JSONObject(Gson().toJson(visits))
    sendRequest(app, Request.Method.POST, baseUrl + updateStatistics, json, responseListener, errorListener)
}

