package at.fhhagenberg.sqelevator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ElevatorMainTest {

    @Mock
    private MqttWrapper mockMqttClient;
    @Mock
    private ElevatorMain mockElevatorMain;

    @BeforeEach
    void setUp() throws RemoteException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testConstructor() {
        mockElevatorMain = new ElevatorMain("", "");
        assertNotNull(mockElevatorMain);
    }

    @Test
    void testInit() {
        doNothing().when(mockMqttClient).subscribe(anyString());
        when(mockElevatorMain.getMQTTClient()).thenReturn(mockMqttClient);

        mockElevatorMain.init();

        verify(mockElevatorMain).init();
    }
}