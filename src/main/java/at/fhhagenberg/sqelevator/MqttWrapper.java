package at.fhhagenberg.sqelevator;

import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.eclipse.paho.mqttv5.client.MqttClient;

public class MqttWrapper {
    private static final Logger LOGGER = Logger.getLogger(MqttWrapper.class.getName());
    private final MqttClient client;
    private final String controllerTopic;

    private static final String MqttPublishFail = "Error publishing MQTT message: ";

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
            LOGGER.log(Level.SEVERE, String.format("%s: %s", MqttPublishFail, e.getMessage()));
        }
    }

    public void publishMQTTMessage(String topic, String message) {
        try {
            //System.out.println(this.controllerTopic + topic + " : " + message);
            client.publish(this.controllerTopic + topic, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            LOGGER.log(Level.SEVERE, String.format("%s: %s", MqttPublishFail, e.getMessage()));
        }
    }

    public void subscribe(String topic) {
        try {
            client.subscribe(topic, 0);
        } catch(MqttException e) {
            LOGGER.log(Level.SEVERE, String.format("%s: %s", MqttPublishFail, e.getMessage()));
        }
    }

}
