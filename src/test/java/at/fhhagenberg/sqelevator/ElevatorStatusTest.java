package at.fhhagenberg.sqelevator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import sqelevator.IElevator;

import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ElevatorStatusTest {


    @Mock
    private IElevator mockElevatorController;

    @Mock
    private MqttWrapper mockMqttClient;

    private ElevatorStatus elevatorStatus;

    @Captor
    private ArgumentCaptor<String> topicCaptor;
    @Captor
    private ArgumentCaptor<String> messageCaptor;

    @BeforeEach
    void setUp() throws RemoteException {
        MockitoAnnotations.initMocks(this);
        when(mockElevatorController.getFloorNum()).thenReturn(3);
        elevatorStatus = new ElevatorStatus(mockMqttClient, mockElevatorController, 0);
    }


    @Test
    void testConstructor() throws RemoteException {
        MqttWrapper mockClient = mock(MqttWrapper.class);
        IElevator mockElevator = mock(IElevator.class);
        int elevatorNumber = 1;

        ElevatorStatus elevatorStatus = new ElevatorStatus(mockClient, mockElevator, elevatorNumber);

        assertEquals(elevatorNumber, elevatorStatus.elevatorNum.get());
    }

    @Test
    void testUpdateAndPublishIfChanged() {
        AtomicInteger currentValue = new AtomicInteger(5);
        int newValue = 10;
        String topic = "testTopic";

        elevatorStatus._updateAndPublishIfChanged(topic, currentValue, newValue, false);

        verify(mockMqttClient).publishMQTTMessage(elevatorStatus.elevatorNum + "/" + topic, Integer.toString(newValue));

        assertEquals(newValue, currentValue.get());

        elevatorStatus._updateAndPublishIfChanged(topic, currentValue, 10, true);

        verify(mockMqttClient).publishMQTTMessage(elevatorStatus.elevatorNum + "/" + topic, Integer.toString(newValue));

        assertEquals(10, currentValue.get());

        elevatorStatus._updateAndPublishIfChanged(topic, currentValue, 20, true);

        verify(mockMqttClient).publishMQTTMessage(elevatorStatus.elevatorNum + "/" + topic, Integer.toString(newValue));

        assertEquals(20, currentValue.get());
    }

    @Test
    void testCheckStatusUTDfalseBTNtrue() throws RemoteException {
        Answer<String> answer = new Answer<String>() {
            public String answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                return args[0] + ": " + args[1];
            }
        };

        when(mockElevatorController.getElevatorFloor(anyInt())).thenReturn(1);
        when(mockElevatorController.getElevatorPosition(anyInt())).thenReturn(2);
        when(mockElevatorController.getTarget(anyInt())).thenReturn(3);
        when(mockElevatorController.getCommittedDirection(anyInt())).thenReturn(4);
        when(mockElevatorController.getElevatorDoorStatus(anyInt())).thenReturn(5);

        //when(mockElevatorController.getFloorNum()).thenReturn(3);
        when(mockElevatorController.getElevatorButton(anyInt(), anyInt())).thenReturn(true);

        elevatorStatus.checkStatus(false);
        assertEquals(1, mockElevatorController.getElevatorFloor(3));

        verify(mockMqttClient, times(8)).publishMQTTMessage(topicCaptor.capture(), messageCaptor.capture());

        List<String> capturedTopics = topicCaptor.getAllValues();
        List<String> capturedMessages = messageCaptor.getAllValues();

        String MQTTmessage = capturedTopics.get(0) + ": " + capturedMessages.get(0);
        assertEquals(elevatorStatus.elevatorNum.get()+"/floorNum: 1", MQTTmessage);

        MQTTmessage = capturedTopics.get(1) + ": " + capturedMessages.get(1);
        assertEquals(elevatorStatus.elevatorNum.get()+"/position: 2", MQTTmessage);

        MQTTmessage = capturedTopics.get(2) + ": " + capturedMessages.get(2);
        assertEquals(elevatorStatus.elevatorNum.get()+"/target: 3", MQTTmessage);

        MQTTmessage = capturedTopics.get(3) + ": " + capturedMessages.get(3);
        assertEquals(elevatorStatus.elevatorNum.get()+"/committed_direction: 4", MQTTmessage);

        MQTTmessage = capturedTopics.get(4) + ": " + capturedMessages.get(4);
        assertEquals(elevatorStatus.elevatorNum.get()+"/door_status: 5", MQTTmessage);

        // Assertions for the MQTT messages inside the loop
        for (int i = 0; i < mockElevatorController.getFloorNum(); i++) {
            String expectedTopic = elevatorStatus.elevatorNum.get() + "/FloorButton/" + i;
            String expectedMessage = "true"; // Since we mocked getElevatorButton to return true
            MQTTmessage = capturedTopics.get(5 + i) + ": " + capturedMessages.get(5 + i);
            assertEquals(expectedTopic + ": " + expectedMessage, MQTTmessage);
        }
    }

    @Test
    void testCheckStatusUTDtrueBTNtrue() throws RemoteException {
        Answer<String> answer = new Answer<String>() {
            public String answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                return args[0] + ": " + args[1];
            }
        };

        when(mockElevatorController.getElevatorFloor(anyInt())).thenReturn(1);
        when(mockElevatorController.getElevatorPosition(anyInt())).thenReturn(2);
        when(mockElevatorController.getTarget(anyInt())).thenReturn(3);
        when(mockElevatorController.getCommittedDirection(anyInt())).thenReturn(4);
        when(mockElevatorController.getElevatorDoorStatus(anyInt())).thenReturn(5);

        //when(mockElevatorController.getFloorNum()).thenReturn(3);
        when(mockElevatorController.getElevatorButton(anyInt(), anyInt())).thenReturn(true);

        elevatorStatus.checkStatus(true);
        assertEquals(1, mockElevatorController.getElevatorFloor(3));

        verify(mockMqttClient, times(8)).publishMQTTMessage(topicCaptor.capture(), messageCaptor.capture());

        List<String> capturedTopics = topicCaptor.getAllValues();
        List<String> capturedMessages = messageCaptor.getAllValues();

        String MQTTmessage = capturedTopics.get(0) + ": " + capturedMessages.get(0);
        assertEquals(elevatorStatus.elevatorNum.get()+"/floorNum: 1", MQTTmessage);

        MQTTmessage = capturedTopics.get(1) + ": " + capturedMessages.get(1);
        assertEquals(elevatorStatus.elevatorNum.get()+"/position: 2", MQTTmessage);

        MQTTmessage = capturedTopics.get(2) + ": " + capturedMessages.get(2);
        assertEquals(elevatorStatus.elevatorNum.get()+"/target: 3", MQTTmessage);

        MQTTmessage = capturedTopics.get(3) + ": " + capturedMessages.get(3);
        assertEquals(elevatorStatus.elevatorNum.get()+"/committed_direction: 4", MQTTmessage);

        MQTTmessage = capturedTopics.get(4) + ": " + capturedMessages.get(4);
        assertEquals(elevatorStatus.elevatorNum.get()+"/door_status: 5", MQTTmessage);

        // Assertions for the MQTT messages inside the loop
        for (int i = 0; i < mockElevatorController.getFloorNum(); i++) {
            String expectedTopic = elevatorStatus.elevatorNum.get() + "/FloorButton/" + i;
            String expectedMessage = "true"; // Since we mocked getElevatorButton to return true
            MQTTmessage = capturedTopics.get(5 + i) + ": " + capturedMessages.get(5 + i);
            assertEquals(expectedTopic + ": " + expectedMessage, MQTTmessage);
        }
    }

    @Test
    void testCheckStatusUTDtrueBTNfalse() throws RemoteException {
        Answer<String> answer = new Answer<String>() {
            public String answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                return args[0] + ": " + args[1];
            }
        };

        when(mockElevatorController.getElevatorFloor(anyInt())).thenReturn(1);
        when(mockElevatorController.getElevatorPosition(anyInt())).thenReturn(2);
        when(mockElevatorController.getTarget(anyInt())).thenReturn(3);
        when(mockElevatorController.getCommittedDirection(anyInt())).thenReturn(4);
        when(mockElevatorController.getElevatorDoorStatus(anyInt())).thenReturn(5);

        //when(mockElevatorController.getFloorNum()).thenReturn(3);
        when(mockElevatorController.getElevatorButton(anyInt(), anyInt())).thenReturn(false);

        elevatorStatus.checkStatus(true);
        assertEquals(1, mockElevatorController.getElevatorFloor(3));

        verify(mockMqttClient, times(5)).publishMQTTMessage(topicCaptor.capture(), messageCaptor.capture());

        List<String> capturedTopics = topicCaptor.getAllValues();
        List<String> capturedMessages = messageCaptor.getAllValues();

        String MQTTmessage = capturedTopics.get(0) + ": " + capturedMessages.get(0);
        assertEquals(elevatorStatus.elevatorNum.get()+"/floorNum: 1", MQTTmessage);

        MQTTmessage = capturedTopics.get(1) + ": " + capturedMessages.get(1);
        assertEquals(elevatorStatus.elevatorNum.get()+"/position: 2", MQTTmessage);

        MQTTmessage = capturedTopics.get(2) + ": " + capturedMessages.get(2);
        assertEquals(elevatorStatus.elevatorNum.get()+"/target: 3", MQTTmessage);

        MQTTmessage = capturedTopics.get(3) + ": " + capturedMessages.get(3);
        assertEquals(elevatorStatus.elevatorNum.get()+"/committed_direction: 4", MQTTmessage);

        MQTTmessage = capturedTopics.get(4) + ": " + capturedMessages.get(4);
        assertEquals(elevatorStatus.elevatorNum.get()+"/door_status: 5", MQTTmessage);

    }

    @Test
    void testCheckStatusUTDfalseBTNfalse() throws RemoteException {
        Answer<String> answer = new Answer<String>() {
            public String answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                return args[0] + ": " + args[1];
            }
        };

        when(mockElevatorController.getElevatorFloor(anyInt())).thenReturn(1);
        when(mockElevatorController.getElevatorPosition(anyInt())).thenReturn(2);
        when(mockElevatorController.getTarget(anyInt())).thenReturn(3);
        when(mockElevatorController.getCommittedDirection(anyInt())).thenReturn(4);
        when(mockElevatorController.getElevatorDoorStatus(anyInt())).thenReturn(5);

        //when(mockElevatorController.getFloorNum()).thenReturn(3);
        when(mockElevatorController.getElevatorButton(anyInt(), anyInt())).thenReturn(false);

        elevatorStatus.checkStatus(false);
        assertEquals(1, mockElevatorController.getElevatorFloor(3));

        verify(mockMqttClient, times(8)).publishMQTTMessage(topicCaptor.capture(), messageCaptor.capture());

        List<String> capturedTopics = topicCaptor.getAllValues();
        List<String> capturedMessages = messageCaptor.getAllValues();

        String MQTTmessage = capturedTopics.get(0) + ": " + capturedMessages.get(0);
        assertEquals(elevatorStatus.elevatorNum.get()+"/floorNum: 1", MQTTmessage);

        MQTTmessage = capturedTopics.get(1) + ": " + capturedMessages.get(1);
        assertEquals(elevatorStatus.elevatorNum.get()+"/position: 2", MQTTmessage);

        MQTTmessage = capturedTopics.get(2) + ": " + capturedMessages.get(2);
        assertEquals(elevatorStatus.elevatorNum.get()+"/target: 3", MQTTmessage);

        MQTTmessage = capturedTopics.get(3) + ": " + capturedMessages.get(3);
        assertEquals(elevatorStatus.elevatorNum.get()+"/committed_direction: 4", MQTTmessage);

        MQTTmessage = capturedTopics.get(4) + ": " + capturedMessages.get(4);
        assertEquals(elevatorStatus.elevatorNum.get()+"/door_status: 5", MQTTmessage);

        // Assertions for the MQTT messages inside the loop
        for (int i = 0; i < mockElevatorController.getFloorNum(); i++) {
            String expectedTopic = elevatorStatus.elevatorNum.get() + "/FloorButton/" + i;
            String expectedMessage = "false"; // Since we mocked getElevatorButton to return true
            MQTTmessage = capturedTopics.get(5 + i) + ": " + capturedMessages.get(5 + i);
            assertEquals(expectedTopic + ": " + expectedMessage, MQTTmessage);
        }
    }
}
