package ml.robiot.mqttflow.models

import org.eclipse.paho.client.mqttv3.IMqttToken

data class IMqttAction(val asyncActionToken: IMqttToken?, val exception: Throwable?)
