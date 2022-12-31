package ml.robiot.mqttflow

class MqttSubscribe /*@Inject constructor(private val mqttClient: MqttAndroidClient) {
    companion object {
        const val TAG_SUBSCRIBE = "MqttSubscribe"
    }
    suspend operator fun invoke(topic: String, qos: Int): Flow<Boolean> = callbackFlow {
        val iMqttActionListener =  object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d(TAG_SUBSCRIBE, "The subscription was successful")
                trySend(true)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d(TAG_SUBSCRIBE, "Connect failed with exception: $exception")
                trySend(false)
            }
        }
        val subscribe = mqttClient.subscribe(topic, qos, null, iMqttActionListener)
        awaitClose { subscribe.isComplete }
    }
}*/