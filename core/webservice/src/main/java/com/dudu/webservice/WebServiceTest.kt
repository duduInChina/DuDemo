package com.dudu.webservice

import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import kotlin.concurrent.thread

/**
 * 功能介绍
 * Created by Dzc on 2023/5/12.
 */
class WebServiceTest {
    fun test() = run {
        thread {

            val request = SoapObject("http://tempurl.org", "uf_test")
            request.addProperty("ai_1", 1)
            val envelope = SoapSerializationEnvelope(
                SoapEnvelope.VER11
            )
            envelope.bodyOut = request
            envelope.dotNet = true
            envelope.setOutputSoapObject(request)

            val ht = HttpTransportSE("")
            ht.debug = true
            try {
                // 请求WS
                ht.call("http://tempurl.org/uf_test", envelope)
                if (envelope.response != null) {
                    // 获得WS函数返回值信息
                    println(envelope.response.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println(e.message)
            }
        }
    }
}