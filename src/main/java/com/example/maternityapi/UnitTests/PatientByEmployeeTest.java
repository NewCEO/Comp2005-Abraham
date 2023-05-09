package com.example.maternityapi.UnitTests;

import com.example.maternityapi.Controllers.PatientByEmployee;
import com.example.maternityapi.Models.Patient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class PatientByEmployeeTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PatientByEmployee controller;

    private HttpHeaders headers;

    @Before
    public void setUp() {
        headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetPatientsByEmployee() throws Exception {
        // Mock the response from the allocations API
        String allocationsUrl = "https://web.socem.plymouth.ac.uk/COMP2005/api/Allocations";
        String allocationsResponseBody = "[{\"id\": 1, \"employeeID\": 123, \"admissionID\": 456}]";
        ResponseEntity<String> allocationsResponse = new ResponseEntity<>(allocationsResponseBody, headers, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(eq(allocationsUrl), eq(String.class))).thenReturn(allocationsResponse);

        // Mock the response from the admissions API
        String admissionsUrl = "https://web.socem.plymouth.ac.uk/COMP2005/api/Admissions";
        String admissionsResponseBody = "[{\"id\": 456, \"patientID\": 789, \"admissionDate\": \"2022-04-01T12:34:56\"}]";
        ResponseEntity<String> admissionsResponse = new ResponseEntity<>(admissionsResponseBody, headers, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(eq(admissionsUrl), eq(String.class))).thenReturn(admissionsResponse);

        // Mock the response from the patients API
        String patientsUrl = "https://web.socem.plymouth.ac.uk/COMP2005/api/Patients";
        String patientsResponseBody = "[{\"id\": 789, \"surname\": \"Alice\", \"forename\": \"Smith\", \"nhsNumber\": \"1113335555\"}]";
        ResponseEntity<String> patientsResponse = new ResponseEntity<>(patientsResponseBody, headers, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(eq(patientsUrl), eq(String.class))).thenReturn(patientsResponse);


        // Call the controller method
        ResponseEntity<Patient[]> response = controller.getPatientsByEmployee(123);


        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().length);
        assertEquals(789, response.getBody()[0].getId());
        assertEquals("Alice", response.getBody()[0].getForename());
        assertEquals("Smith", response.getBody()[0].getSurname());
        assertEquals("1113335555", response.getBody()[0].getNhsNumber());
    }

    @Test
    public void testGetPatientsByEmployeeNoAllocations() throws Exception {
        // Mock a 404 response from the allocations API since the employee has no allocations
        String allocationsUrl = "https://web.socem.plymouth.ac.uk/COMP2005/api/Allocations";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Accept", "application/json");
        ResponseEntity<String> allocationsResponse = new ResponseEntity<>("Not Found", headers, HttpStatus.NOT_FOUND);
        Mockito.when(restTemplate.getForEntity(eq(allocationsUrl), eq(String.class))).thenReturn(allocationsResponse);

        // Custom employee id value
        int employeeId = 456;

        try {
            ResponseEntity<Patient[]> response = controller.getPatientsByEmployee(employeeId);

            // Verify the response
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Not Found", response.getBody());
            assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        } catch (NullPointerException e) {
            fail("An unexpected NullPointerException occurred: " + e.getMessage());
        }
    }


}

