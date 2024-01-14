package at.fhhagenberg.sqelevator;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MqttAdapter {
    private MqttWrapper mqttWrapper;
    private ExecutorService executorService;
    private final int pollingInterval = 250;
    private final BuildingStatus buildingStatus;

    public MqttAdapter(String rmiConnectionString, String mqttConnectionString, String clientId) {
        mqttWrapper = getMQTTClient(mqttConnectionString, clientId);
        mqttWrapper.publishMQTTMessage("ElevatorController", "RMI Connection established.");

        buildingStatus = new BuildingStatus(mqttWrapper, rmiConnectionString);
        try {
            buildingStatus.Init();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    protected MqttWrapper getMQTTClient(String mqttConnectionString, String clientId) {
        if(mqttConnectionString.isEmpty()) {
            mqttConnectionString = "tcp://localhost:1883";
        }
        if(clientId.isEmpty()){
            clientId = "mqttAdapter";
        }

        mqttWrapper = new MqttWrapper(mqttConnectionString, clientId);  //URI, ClientId, Persistence
        return mqttWrapper;
    }

    public void startPollingElevatorState() {
        this.executorService = Executors.newSingleThreadExecutor();
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
