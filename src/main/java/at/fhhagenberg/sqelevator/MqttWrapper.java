package at.fhhagenberg.sqelevator;

import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;

import org.eclipse.paho.mqttv5.client.MqttClient;

public class MqttWrapper {
    private final MqttClient client;

    public MqttWrapper(String mqttConnectionString, String clientId)
    {
        try {
            this.client = new MqttClient(mqttConnectionString, clientId, new MemoryPersistence());  //URI, ClientId, Persistence
            this.client.connect();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public void publishRetainedMQTTMessage(String topic, String message) {
        try {
            MqttMessage mes = new MqttMessage(message.getBytes());
            mes.setRetained(true);
            client.publish(topic, mes);
        } catch (MqttException e) {
            System.err.println("Error publishing MQTT message: " + e.getMessage());
        }
    }

    public void publishMQTTMessage(String topic, String message) {
        try {
            client.publish(topic, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            System.err.println("Error publishing MQTT message: " + e.getMessage());
        }
    }

    public void subscribe(String topic) {
        // Needs implementation! TODO

    }


}
