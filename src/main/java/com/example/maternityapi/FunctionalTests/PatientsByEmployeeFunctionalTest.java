package com.example.maternityapi.FunctionalTests;


import com.example.maternityapi.Models.Admission;
import com.example.maternityapi.Models.Allocation;
import com.example.maternityapi.Models.Patient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
//import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
public class PatientsByEmployeeFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @Value("${wiremock.server.baseUrl}")
    private String wireMockBaseUrl;

    @Test
    public void testGetPatientsByEmployee() throws Exception {
        // Setup
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Allocation allocation = new Allocation(3, 1,1, new Date(1641530400000L), new Date(1641534000000L) );
        allocation.setEmployeeID(1);
        allocation.setAdmissionID(1);

        Admission admission = new Admission();
        admission.setId(1);
        admission.setPatientID(1);

        Patient patient = new Patient();
        patient.setId(1);
        patient.setForename("John");
        patient.setSurname("Doe");
        patient.setNhsNumber("3094764863");


        List<Allocation> allocations = new ArrayList<>();
        allocations.add(allocation);

        List<Admission> admissions = new ArrayList<>();
        admissions.add(admission);

        List<Patient> patients = new ArrayList<>();
        patients.add(patient);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

        String allocationsResponseBody = mapper.writeValueAsString(allocations);
        String admissionsResponseBody = mapper.writeValueAsString(admissions);
        String patientsResponseBody = mapper.writeValueAsString(patients);

        when(restTemplate.exchange(eq("https://web.socem.plymouth.ac.uk/COMP2005/api/Allocations"), eq(HttpMethod.GET), eq(entity), eq(String.class)))
                .thenReturn(new ResponseEntity<>(allocationsResponseBody, HttpStatus.OK));
        when(restTemplate.exchange(eq("https://web.socem.plymouth.ac.uk/COMP2005/api/Admissions"), eq(HttpMethod.GET), eq(entity), eq(String.class)))
                .thenReturn(new ResponseEntity<>(admissionsResponseBody, HttpStatus.OK));
        when(restTemplate.exchange(eq("https://web.socem.plymouth.ac.uk/COMP2005/api/Patients"), eq(HttpMethod.GET), eq(entity), eq(String.class)))
                .thenReturn(new ResponseEntity<>(patientsResponseBody, HttpStatus.OK));

        // Test
        mockMvc.perform(get(wireMockBaseUrl + "/employees/1/patientsByEmployeeID"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].forename").value("John"))
                .andExpect(jsonPath("$[0].surname").value("Doe"))
                .andExpect(jsonPath("$[0].nhsNumber").value("3094764863"));






        // Verify
        verify(restTemplate, times(1)).exchange(eq("https://web.socem.plymouth.ac.uk/COMP2005/api/Allocations"), eq(HttpMethod.GET), eq(entity), eq(String.class));
        verify(restTemplate, times(1)).exchange(eq("https://web.socem.plymouth.ac.uk/COMP2005/api/Admissions"), eq(HttpMethod.GET), eq(entity), eq(String.class));
    }
}