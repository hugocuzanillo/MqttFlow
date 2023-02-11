package ml.robiot.mqttflow.models

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttMessage

data class InnerMqttCallback(
    val topic: String?,
    val message: MqttMessage?,
    val token: IMqttDeliveryToken?,
    val cause: Throwable?
)
