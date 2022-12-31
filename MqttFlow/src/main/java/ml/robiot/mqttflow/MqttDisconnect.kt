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
import javax.inject.Inject

class MqttDisconnect @Inject constructor(private val mqttClient: MqttAndroidClient) {
    companion object {
        private const val TAG = "MqttDisconnect"
    }
    suspend operator fun invoke(): Flow<Boolean> = callbackFlow {
        val iMqttActionListener = object : IMqttActionListener {
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
        val disconnect = mqttClient.disconnect(null, iMqttActionListener)
        awaitClose { disconnect.isComplete }
    }
}