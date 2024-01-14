package at.fhhagenberg.sqelevator;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MqttAdapter implements MqttCallback {
    private MqttWrapper mqttWrapper;
    private static final int POLLING_INTERVAL = 250;
    private static final String CONTROLLER_TOPIC_MAIN = "ElevatorControllerMain/";
    private static final String CONTROLLER_TOPIC_RMI = "ElevatorControllerRMI/";
    private final BuildingStatus buildingStatus;
    private static final Logger LOGGER = Logger.getLogger(MqttWrapper.class.getName());

    private boolean initDone;

    public MqttAdapter(String rmiConnectionString, String mqttConnectionString, String clientId) {
        mqttWrapper = getMQTTClient(mqttConnectionString, clientId);
        mqttWrapper.publishMQTTMessage("Connect", "RMI Connection established.");

        buildingStatus = new BuildingStatus(mqttWrapper, rmiConnectionString);

        buildingStatus.init();

        initDone = false;

        mqttWrapper.subscribe(CONTROLLER_TOPIC_MAIN + "#");
    }

    protected MqttWrapper getMQTTClient(String mqttConnectionString, String clientId) {
        if(mqttConnectionString.isEmpty()) {
            mqttConnectionString = "tcp://localhost:1883";
        }
        if(clientId.isEmpty()){
            clientId = "mqttAdapter";
        }

        mqttWrapper = new MqttWrapper(mqttConnectionString, clientId, CONTROLLER_TOPIC_RMI, this);  //URI, ClientId, Persistence
        return mqttWrapper;
    }

    public void startPollingElevatorState() {
        ExecutorService executorService;
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            while (true) {
                try {
                    Thread.sleep(POLLING_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                if (initDone) {
                    buildingStatus.sendStatus();
                }
            }
        });
    }

    @Override
    public void disconnected(MqttDisconnectResponse var1){
        // not needed
    }
    @Override
    public void mqttErrorOccurred(MqttException var1){
        // not needed
    }
    @Override
    public void messageArrived(String var1, MqttMessage var2) throws Exception {
        //LOGGER.log(Level.INFO, String.format("received: %s ~ %s", var1, var2));
        String[] topics = var1.split("/");
        if(topics.length < 2) {
            //LOGGER.log(Level.INFO, String.format("ignoring: %s ~ %s", var1, var2));
            return;
        }
        if(topics[1].equals("InitDone")) {
            initDone = true;
            return;
        }

        // Topics with deeps 3
        if(topics.length < 3) {
            //LOGGER.log(Level.INFO, String.format("ignoring: %s ~ %s", var1, var2));
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

        LOGGER.log(Level.INFO, "Unhandeled");
    }
    @Override
    public void deliveryComplete(IMqttToken var1){
        // not needed
    }
    @Override
    public void connectComplete(boolean var1, String var2){
        // not needed
    }
    @Override
    public void authPacketArrived(int var1, MqttProperties var2){
        // not needed
    }
}
