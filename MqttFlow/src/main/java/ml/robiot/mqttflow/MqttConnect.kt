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
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import java.io.InputStream
import javax.inject.Inject

class MqttConnect @Inject constructor(private val mqttClient: MqttAndroidClient, private val sslSocketFactory: SslSocketFactory) {
    companion object {
        private const val TAG = "MqttConnect"
    }
    suspend operator fun invoke(username: String, password: String, caCrtFileI: InputStream): Flow<Boolean> = callbackFlow {
        val options = MqttConnectOptions()
        options.socketFactory = sslSocketFactory(caCrtFileI)
        options.userName = username
        options.password = password.toCharArray()
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
        val connect = mqttClient.connect(options, null, iMqttActionListener)
        awaitClose { connect.isComplete }
    }
}