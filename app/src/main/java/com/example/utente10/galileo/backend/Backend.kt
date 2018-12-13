package com.example.utente10.galileo.backend


import com.example.utente10.galileo.Application
import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.utente10.galileo.example.macroareasList

//request url
val macroareaUrl = ""

interface ResponseListener {
    fun onResponse(response: String)
}

interface ErrorListener {
    fun onError(error: String?)
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

fun getMacroareas(app: Context, responseListener: ResponseListener, errorListener: ErrorListener){

    //val map = HashMap<String, String>()
    //sendRequest(app, Request.Method.GET, macroareaUrl, map, responseListener, errorListener)
    responseListener.onResponse(macroareasList);
}

