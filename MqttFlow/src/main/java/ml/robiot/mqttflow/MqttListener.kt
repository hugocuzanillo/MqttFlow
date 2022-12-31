package ml.robiot.mqttflow

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import javax.inject.Inject

class MqttListener @Inject constructor(private val mqttClient: MqttAndroidClient) {
    companion object {
        private const val TAG = "MqttListener"
    }
    suspend operator fun invoke(): Flow<MqttModel?> = callbackFlow {
        val callback = object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                trySendBlocking(null).onFailure {
                    Log.d(TAG, "On Failure by: $it")
                }
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                trySendBlocking(MqttModel(topic = topic, message = message)).onFailure {
                    Log.d(TAG, "On Failure by: $it")
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        }
        mqttClient.setCallback(callback)
        awaitClose {
            callback.connectionLost(cause = null)
        }
    }
}