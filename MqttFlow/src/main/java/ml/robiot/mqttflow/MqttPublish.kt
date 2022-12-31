package ml.robiot.mqttflow

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttMessage
import javax.inject.Inject

class MqttPublish @Inject constructor(private val mqttClient: MqttAndroidClient){
    companion object {
        private const val TAG = "MqttPublish"
    }
    suspend operator fun invoke(topic: String, msg: String, qos: Int, retained: Boolean): Flow<Boolean> = callbackFlow {
        val message = MqttMessage()
        message.payload = msg.replace("\n", " ").toByteArray(charset("UTF-8"))
        message.qos = qos
        message.isRetained = retained
        val iMqttActionListener = object: IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                trySendBlocking(true).onFailure {
                    Log.d(TAG, "On Failure by: $it")
                }
            }
            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                trySendBlocking(false).onFailure {
                    Log.d(TAG, "On Failure by: $it")
                }
            }
        }
        val publish = mqttClient.publish(topic, message, null, iMqttActionListener)
        awaitClose { publish.isComplete }
    }
}