package at.fhhagenberg.sqelevator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sqelevator.IElevator;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// NOT DONE!!!
class BuildingStatusTest {

    @Mock
    private MqttWrapper mockMqttClient;

    @Mock
    private IElevator controller;

    private BuildingStatus status = null;

    @Mock
    private BuildingStatus mockStatus = null;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);

        status = new BuildingStatus(mockMqttClient, "");
        mockStatus = new BuildingStatus(mockMqttClient, "");
    }

    @Test
    void testConstructor(){
        assertNotNull(status);
        assertNotNull(mockStatus);
    }
}
