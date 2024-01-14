package at.fhhagenberg.sqelevator;

import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;

import org.eclipse.paho.mqttv5.client.MqttClient;

public class MqttWrapper {
    private final MqttClient client;
    private final String controllerTopic;

    public MqttWrapper(String mqttConnectionString, String clientId, String controllerTopic, MqttCallback cb)
    {
        try {
            this.client = new MqttClient(mqttConnectionString, clientId, new MemoryPersistence());  //URI, ClientId, Persistence
            this.controllerTopic = controllerTopic;
            this.client.connect();

            // set Callbacks to receive Messages
            client.setCallback(cb);

        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public void publishRetainedMQTTMessage(String topic, String message) {
        try {
            MqttMessage mes = new MqttMessage(message.getBytes());
            mes.setRetained(true);
            client.publish(this.controllerTopic + topic, mes);
        } catch (MqttException e) {
            System.err.println("Error publishing MQTT message: " + e.getMessage());
        }
    }

    public void publishMQTTMessage(String topic, String message) {
        try {
            //System.out.println(this.controllerTopic + topic + " : " + message);
            client.publish(this.controllerTopic + topic, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            System.err.println("Error publishing MQTT message: " + e.getMessage());
        }
    }

    public void subscribe(String topic) {
        try {
            client.subscribe(topic, 0);
        } catch(MqttException e) {
            System.err.println("Error publishing MQTT message: " + e.getMessage());
        }
    }

}
