package at.fhhagenberg.sqelevator;

import org.eclipse.paho.mqttv5.common.MqttException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;

public class MqttAdapter {
    private MqttWrapper mqttWrapper;
    private ExecutorService executorService;
    private final int pollingInterval = 250;
    private final BuildingStatus buildingStatus;

    public MqttAdapter(String rmiConnectionString, String mqttConnectionString, String clientId) throws MqttException {
        mqttWrapper = getMQTTClient(mqttConnectionString, clientId);
        mqttWrapper.publishMQTTMessage("ElevatorController", "RMI Connection established.");

        buildingStatus = new BuildingStatus(mqttWrapper, rmiConnectionString);
        try {
            buildingStatus.Init();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        startPollingElevatorState();
    }

    protected MqttWrapper getMQTTClient(String mqttConnectionString, String clientId) {
        if(mqttConnectionString.isEmpty()) {
            mqttConnectionString = "tcp://localhost:1883";
        }
        if(clientId.isEmpty()){
            clientId = "building_controller_client";
        }

        mqttWrapper = new MqttWrapper(mqttConnectionString, clientId);  //URI, ClientId, Persistence
        return mqttWrapper;
    }

    private void startPollingElevatorState() {
        this.executorService.submit(() -> {
            while (true) {
                try {
                    Thread.sleep(pollingInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                buildingStatus.sendStatus();
            }
        });
    }
}
