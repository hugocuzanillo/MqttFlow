package ml.robiot.mqttflow

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import ml.robiot.mqttflow.models.InnerMqttCallback
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import javax.inject.Inject

class MqttListener @Inject constructor(private val mqttClient: MqttAndroidClient) {
    companion object {
        private const val TAG = "MqttListener"
    }
    suspend operator fun invoke(): Flow<InnerMqttCallback> = callbackFlow {
        val callback = object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                trySendBlocking(InnerMqttCallback(null, null, null, cause)).onFailure {
                    Log.d(TAG, "On Failure by: $it")
                }
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                trySendBlocking(InnerMqttCallback(topic, message, null, null)).onFailure {
                    Log.d(TAG, "On Failure by: $it")
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                trySendBlocking(InnerMqttCallback(null, null, token, null)).onFailure {
                    Log.d(TAG, "On Failure by: $it")
                }
            }
        }
        mqttClient.setCallback(callback)
        awaitClose {
            callback.connectionLost(cause = null)
        }
    }.flowOn(Dispatchers.IO)
}