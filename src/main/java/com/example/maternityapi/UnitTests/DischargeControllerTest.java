package com.example.maternityapi.UnitTests;

import com.example.maternityapi.Controllers.DischargeController;
import com.example.maternityapi.Models.Admission;
import com.example.maternityapi.Models.Patient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DischargeControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DischargeController dischargeController;

    @Test
    public void testGetPatientsDischargedWithinThreeDays() {
        // Setup mock data
        Admission admission1 = new Admission();
        admission1.setAdmissionDate(new Date(1641093600000L)); // 02 Jan 2022
        admission1.setDischargeDate(new Date(1641352800000L)); // 05 Jan 2022
        admission1.setPatientID(1);

        Admission admission2 = new Admission();
        admission2.setAdmissionDate(new Date(1641650400000L)); // 08 Jan 2022
        admission2.setDischargeDate(new Date(1641823200000L)); // 10 Jan 2022
        admission2.setPatientID(2);

        Admission admission3 = new Admission();
        admission3.setAdmissionDate(new Date(1643215200000L)); // 26 Jan 2022
        admission3.setDischargeDate(new Date(1643560800000L)); // 31 Jan 2022
        admission3.setPatientID(3);

        List<Admission> admissions = new ArrayList<>();
        admissions.add(admission1);
        admissions.add(admission2);
        admissions.add(admission3);

        Patient patient1 = new Patient();
        patient1.setId(1);
        patient1.setForename("John");
        patient1.setSurname("Doe");
        patient1.setNhsNumber("1113335555");

        Patient patient2 = new Patient();
        patient2.setId(2);
        patient2.setForename("Jane");
        patient2.setSurname("Doe");
        patient2.setNhsNumber("1113335555");

        List<Patient> patients = new ArrayList<>();
        patients.add(patient1);
        patients.add(patient2);

        // Setup mock responses
        ResponseEntity<List<Admission>> admissionsResponse = new ResponseEntity<>(admissions, HttpStatus.OK);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.<HttpMethod>any(), Mockito.<HttpEntity<?>>any(),
                Mockito.<ParameterizedTypeReference<List<Admission>>>any())).thenReturn(admissionsResponse);

        ResponseEntity<Patient> patient1Response = new ResponseEntity<>(patient1, HttpStatus.OK);
        ResponseEntity<Patient> patient2Response = new ResponseEntity<>(patient2, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(Mockito.eq("https://web.socem.plymouth.ac.uk/COMP2005/api/patients/1"), Mockito.eq(Patient.class)))
                .thenReturn(patient1Response);
        Mockito.when(restTemplate.getForEntity(Mockito.eq("https://web.socem.plymouth.ac.uk/COMP2005/api/patients/2"), Mockito.eq(Patient.class)))
                .thenReturn(patient2Response);

        // Call the method being tested
        List<Patient> dischargedPatients = dischargeController.getPatientsDischargedWithinThreeDays();

        // Verify the results
        assertEquals(2, dischargedPatients.size());
        assertEquals("John", dischargedPatients.get(0).getForename());
        assertEquals("Doe", dischargedPatients.get(0).getSurname());
        assertEquals("1113335555", dischargedPatients.get(0).getNhsNumber());
        assertEquals("Jane", dischargedPatients.get(1).getForename());
        assertEquals("Doe", dischargedPatients.get(1).getSurname());
        assertEquals("1113335555", dischargedPatients.get(1).getNhsNumber());


    }}
