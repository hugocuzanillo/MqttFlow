package ml.robiot.mqttflow

import org.eclipse.paho.client.mqttv3.MqttMessage

data class MqttModel(val topic: String?, val message: MqttMessage?)
