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
    private static final int POLLING_INTERVAL = 250;
    private static final String CONTROLLER_TOPIC_MAIN = "ElevatorControllerMain/";
    private static final String CONTROLLER_TOPIC_RMI = "ElevatorControllerRMI/";
    private BuildingStatus buildingStatus;

    public void setBuildingStatus(BuildingStatus b) {
        this.buildingStatus = b;
    }

    private static final Logger LOGGER = Logger.getLogger(MqttAdapter.class.getName());

    private boolean initDone;

    public boolean isInitDone(){
        return initDone;
    }

    private String rmiConnectionString;
    public String getRmiConnectionString(){
        return rmiConnectionString;
    }
    private String mqttConnectionString;

    public String getMqttConnectionString(){
        return mqttConnectionString;
    }
    private String clientID;
    public String getClientID(){
        return clientID;
    }

    public MqttAdapter(String rmiConnectionString, String mqttConnectionString, String clientId) {
        this.rmiConnectionString = rmiConnectionString;
        this.mqttConnectionString = mqttConnectionString;
        this.clientID = clientId;
        if(this.mqttConnectionString.isEmpty()) {
            this.mqttConnectionString = "tcp://localhost:1883";
        }
        if(this.clientID.isEmpty()){
            this.clientID = "mqttAdapter";
        }
        if(this.rmiConnectionString.isEmpty()) {
            this.rmiConnectionString = "rmi://localhost/ElevatorSim";
        }
    }

    public void init() {
        initDone = false;
        MqttWrapper mqttWrapper = getMQTTClient(this.mqttConnectionString, this.clientID);
        mqttWrapper.publishMQTTMessage("Connect", "RMI Connection established.");

        buildingStatus = new BuildingStatus(mqttWrapper, this.rmiConnectionString);
        buildingStatus.connectRMI();

        buildingStatus.init();

        mqttWrapper.subscribe(CONTROLLER_TOPIC_MAIN + "#");
    }

    protected MqttWrapper getMQTTClient(String mqttConnectionString, String clientId) {
        return new MqttWrapper(mqttConnectionString, clientId, CONTROLLER_TOPIC_RMI, this);  // URI, ClientId, Persistence
    }

    public void startRMIPolling() {
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
