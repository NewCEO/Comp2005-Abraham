package com.example.maternityapi.UnitTests;

import com.example.maternityapi.Controllers.MostAdmissions;
import com.example.maternityapi.Models.Admission;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;

import java.util.Date;

import static org.mockito.ArgumentMatchers.eq;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MostAdmissionsTest {

    private RestTemplate restTemplate;
    private MostAdmissions controller;

    @Before
    public void setUp() {
        restTemplate = mock(RestTemplate.class);
        controller = new MostAdmissions(restTemplate);
    }

    @Test
    public void testGetBusiestDayOfWeek() {
        Admission[] admissions = new Admission[]{
                new Admission(1, new Date(122, 0, 1), new Date(122, 0, 3), 1),
                new Admission(2, new Date(122, 0, 3), new Date(122, 0, 4), 1),
                new Admission(3, new Date(122, 0, 4), new Date(122, 0, 6), 1),
                new Admission(4, new Date(122, 0, 4), new Date(122, 0, 6), 1),
                new Admission(5, new Date(122, 0, 6), new Date(122, 0, 7), 1),
                new Admission(6, new Date(122, 0, 7), new Date(122, 0, 8), 1)
        };

        // Mock the response from the REST API
        when(restTemplate.getForObject(anyString(), eq(Admission[].class))).thenReturn(admissions);

        // Call the controller method
        ResponseEntity<String> response = controller.getBusiestDayOfWeek();

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The busiest day of the week for admissions is Tuesday with 2 admissions.", response.getBody());
    }

    @Test
    public void testGetBusiestDayOfWeekWithNoAdmissions() {
        Admission[] admissions = new Admission[]{};

        // Mock the response from the REST API
        when(restTemplate.getForObject(anyString(), eq(Admission[].class))).thenReturn(admissions);

        // Call the controller method
        ResponseEntity<String> response = controller.getBusiestDayOfWeek();

        // Verify the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}

