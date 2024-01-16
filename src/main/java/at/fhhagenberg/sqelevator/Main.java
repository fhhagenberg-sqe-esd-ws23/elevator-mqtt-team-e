package at.fhhagenberg.sqelevator;

public class Main {
    public static void main(String[] args) {
        ElevatorAlgo elevatorAlgo = new ElevatorAlgo("", "");
        elevatorAlgo.init();
        MqttAdapter rmiMqttAdapter = new MqttAdapter("","", "");
        rmiMqttAdapter.init();

        rmiMqttAdapter.startRMIPolling();
        elevatorAlgo.runSim();
    }
}
