package com.example.maternityapi.IntegrationTests;

import com.example.maternityapi.Models.Admission;
import com.example.maternityapi.Models.Allocation;
import com.example.maternityapi.Models.Employee;
import com.example.maternityapi.Models.Patient;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MaternityApiIntegrationTest {

    private static final String BASE_URL = "https://web.socem.plymouth.ac.uk/COMP2005/api";

    @Autowired
    @Qualifier("testRestTemplate")
    private TestRestTemplate testRestTemplate;

    @Test
    public void testGetAdmissions() {
        ResponseEntity<List<Admission>> response = testRestTemplate.exchange(
                BASE_URL + "/Admissions",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Admission>>(){});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }







    @Test
    public void testGetAdmissionById() {
        int id = 1;

        ResponseEntity<Admission> response = testRestTemplate.getForEntity(
                BASE_URL + "/Admissions/" + id,
                Admission.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetAllocations() {
        ResponseEntity<List<Allocation>> response = testRestTemplate.exchange(
                BASE_URL +"/allocations",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Allocation>>(){});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetAllocationById() {
        int allocationId = 1;

        ResponseEntity<Allocation> response = testRestTemplate.getForEntity(
                BASE_URL +"/allocations/" + allocationId,
                Allocation.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetEmployees() {
        ResponseEntity<List<Employee>> response = testRestTemplate.exchange(
                BASE_URL +"/employees",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Employee>>(){});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetEmployeeById() {
        int employeeId = 1;

        ResponseEntity<Employee> response = testRestTemplate.getForEntity(
                BASE_URL +"/employees/" + employeeId,
                Employee.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetPatients() {
        ResponseEntity<List<Patient>> response = testRestTemplate.exchange(
                BASE_URL +"/patients",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Patient>>(){});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetPatientById() {
        int patientId = 1;

        ResponseEntity<Patient> response = testRestTemplate.getForEntity(
                BASE_URL +"/patients/" + patientId,
                Patient.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
