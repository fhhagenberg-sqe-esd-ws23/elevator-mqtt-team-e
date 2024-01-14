package at.fhhagenberg.sqelevator;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.rmi.RemoteException;
import java.sql.SQLOutput;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MqttAdapter implements MqttCallback {
    private MqttWrapper mqttWrapper;
    private ExecutorService executorService;
    private final int pollingInterval = 250;
    private final String controllerTopicMain = "ElevatorControllerMain/";
    private final String controllerTopicRMI = "ElevatorControllerRMI/";
    private final BuildingStatus buildingStatus;

    public MqttAdapter(String rmiConnectionString, String mqttConnectionString, String clientId) {
        mqttWrapper = getMQTTClient(mqttConnectionString, clientId);
        mqttWrapper.publishMQTTMessage("Connect", "RMI Connection established.");

        buildingStatus = new BuildingStatus(mqttWrapper, rmiConnectionString);
        try {
            buildingStatus.Init();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        mqttWrapper.subscribe(controllerTopicMain + "#");
    }

    protected MqttWrapper getMQTTClient(String mqttConnectionString, String clientId) {
        if(mqttConnectionString.isEmpty()) {
            mqttConnectionString = "tcp://localhost:1883";
        }
        if(clientId.isEmpty()){
            clientId = "mqttAdapter";
        }

        mqttWrapper = new MqttWrapper(mqttConnectionString, clientId, controllerTopicRMI, this);  //URI, ClientId, Persistence
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

    @Override
    public void disconnected(MqttDisconnectResponse var1){
    }
    @Override
    public void mqttErrorOccurred(MqttException var1){}
    @Override
    public void messageArrived(String var1, MqttMessage var2) throws Exception {
        System.out.println("received: " + var1 + " ~ " + var2);
        String[] topics = var1.split("/");
        if(topics.length < 3) {
            System.out.println("yeet at 3");
            return;
        }
        if(topics[2].equals("CommittedDirection")) {
            buildingStatus.reportCommitedDirectionToRMI(Integer.parseInt(topics[1]), Integer.parseInt(var2.toString()));
            return;
        }
        if(topics[2].equals("Target")) {
            buildingStatus.reportTargetToRMI(Integer.parseInt(topics[1]), Integer.parseInt(var2.toString()));
            return;
        }
        System.out.println("Unhandeled");
    }
    @Override
    public void deliveryComplete(IMqttToken var1){}
    @Override
    public void connectComplete(boolean var1, String var2){}
    @Override
    public void authPacketArrived(int var1, MqttProperties var2){}
}
