package at.fhhagenberg.sqelevator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// NOT DONE!!!!
class ElevatorMainTest {

    @Mock
    private MqttWrapper mockMqttClient;
    @Mock
    private ElevatorMain ElevatorMain;

    @BeforeEach
    void setUp() throws RemoteException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testConstructor() {
        ElevatorMain = new ElevatorMain("asd", "dsa");

        assertNotNull(ElevatorMain);
        assertEquals("asd", ElevatorMain.getMqttConnectionString());;
        assertEquals("dsa", ElevatorMain.getClientID());
    }

    void testConstructorEmptyStrings() {
        ElevatorMain = new ElevatorMain("", "");

        assertNotNull(ElevatorMain);
        assertEquals("tcp://localhost:1883", ElevatorMain.getMqttConnectionString());;
        assertEquals("building_controller_client", ElevatorMain.getClientID());
    }

    @Test
    void testInit() {
        doNothing().when(mockMqttClient).subscribe(anyString());
        when(ElevatorMain.getMQTTClient()).thenReturn(mockMqttClient);

        ElevatorMain.init();
        verify(ElevatorMain).init();
    }

    @Test
    void getMqttClient() {
        ElevatorMain = new ElevatorMain("asd", "dsa");

        MqttWrapper mqtt = ElevatorMain.getMQTTClient();
        assertEquals(mqtt, ElevatorMain.getMqttWrapper());
    }




}