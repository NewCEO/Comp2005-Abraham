package com.example.maternityapi.UnitTests;

import com.example.maternityapi.Controllers.AverageStay;
import com.example.maternityapi.Models.Admission;
import com.example.maternityapi.Models.Allocation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


import java.util.Date;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;





public class AverageStayTest {
    private RestTemplate restTemplate;
    private AverageStay averageStay;
    private static final String BASE_URL = "https://web.socem.plymouth.ac.uk/COMP2005/api";


    @Before
  public void setUp() {
        restTemplate = mock(RestTemplate.class);
        averageStay = new AverageStay(restTemplate);
    }


    @Test
   public void testGetAverageStayDurationForStaffNoAllocations() {
        // Set up test data
        int employeeId = 123;
        String allocationsUrl = BASE_URL + "/allocations?employeeId=" + employeeId;
        ResponseEntity<Allocation[]> emptyAllocationsResponse = ResponseEntity.ok(new Allocation[0]);
        when(restTemplate.getForEntity(eq(allocationsUrl), eq(Allocation[].class))).thenReturn(emptyAllocationsResponse);

        // Call the method being tested
        ResponseEntity<String> response = averageStay.getAverageStayDurationForStaff(employeeId);

        // Check the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("No allocations found for this staff member.", response.getBody());
    }

    @Test
    public void testGetAverageStayDurationForStaff() {
        int employeeId = 1;

        // Create an allocation with admission id 1 for employee 1
        Allocation[] allocations = new Allocation[]{new Allocation(1, 1, 1, new Date(), new Date())};

        // Create an admission with id 1, admission date Nov 28, 2020 and discharge date Nov 30, 2020 for patient 1
        Admission[] admissions = new Admission[]{new Admission(1, new Date(2020 - 1900, 10, 28, 16, 45), new Date(2020 - 1900, 10, 30, 23, 56), 1)};

        ResponseEntity<Allocation[]> allocationsResponse = new ResponseEntity<>(allocations, HttpStatus.OK);
        ResponseEntity<Admission[]> admissionsResponse = new ResponseEntity<>(admissions, HttpStatus.OK);

        when(restTemplate.getForEntity(eq(BASE_URL + "/allocations?employeeId=" + employeeId), eq(Allocation[].class)))
                .thenReturn(allocationsResponse);
        when(restTemplate.getForEntity(eq(BASE_URL + "/admissions?id=1"), eq(Admission[].class)))
                .thenReturn(admissionsResponse);

        AverageStay controller = new AverageStay (restTemplate);
        ResponseEntity<String> response = controller.getAverageStayDurationForStaff(employeeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The average duration of stay for this employee patient is 2 days.", response.getBody());
    }

    @Test
    public void testGetAverageStayDurationForStaffWithInvalidDates() {
        int employeeId = 1;

        // Include a valid and an invalid admission
        Admission[] admissions = new Admission[]{
                new Admission(1, new Date(122, 0, 1), new Date(122, 0, 3), 1),
                new Admission(2, new Date(122, 0, 3), new Date(122, 0, 2), 1) // invalid
        };

        Allocation[] allocations = new Allocation[]{
                new Allocation(1, 1, employeeId, new Date(122, 0, 1), new Date(122, 0, 3)),
                new Allocation(2, 2, employeeId, new Date(122, 0, 3), new Date(122, 0, 2)) // invalid
        };

        // Calculate average stay duration based on valid records
        int validRecordCount = 0;
        long totalDuration = 0;
        for (int i = 0; i < admissions.length; i++) {
            // Skip invalid records
            if (admissions[i].getAdmissionDate().after(admissions[i].getDischargeDate())) {
                continue;
            }
            // Calculate duration of stay for valid records
            long duration = admissions[i].getDischargeDate().getTime() - admissions[i].getAdmissionDate().getTime();
            totalDuration += duration;
            validRecordCount++;
        }

        double expectedAverageStayDuration = (double) totalDuration / (validRecordCount * 24 * 60 * 60 * 1000);

        // Create a mock object for the RestTemplate and set its response manually
        RestTemplate restTemplateMock = mock(RestTemplate.class);
        ResponseEntity<Allocation[]> allocationsResponse = new ResponseEntity<>(allocations, HttpStatus.OK);
        ResponseEntity<Admission[]> validAdmissionsResponse = new ResponseEntity<>(new Admission[]{admissions[0]}, HttpStatus.OK);
        ResponseEntity<Admission[]> invalidAdmissionsResponse = new ResponseEntity<>(new Admission[]{admissions[1]}, HttpStatus.OK);
        when(restTemplateMock.getForEntity(BASE_URL + "/allocations?employeeId=" + employeeId, Allocation[].class)).thenReturn(allocationsResponse);
        when(restTemplateMock.getForEntity(BASE_URL + "/admissions?id=" + allocations[0].getAdmissionID(), Admission[].class)).thenReturn(validAdmissionsResponse);
        when(restTemplateMock.getForEntity(BASE_URL + "/admissions?id=" + allocations[1].getAdmissionID(), Admission[].class)).thenReturn(invalidAdmissionsResponse);

        // Call the controller method
        AverageStay controller = new AverageStay(restTemplateMock);
        ResponseEntity<String> response = controller.getAverageStayDurationForStaff(employeeId);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Double.toString(expectedAverageStayDuration), response.getBody());
    }


}

