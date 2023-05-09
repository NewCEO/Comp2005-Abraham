package com.example.maternityapi.FunctionalTests;

import com.example.maternityapi.Models.Admission;
import com.example.maternityapi.Models.Patient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application.properties")
public class DischargeControllerFunctionalTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WireMockServer wireMockServer;



    @Value("${wiremock.server.baseUrl}")
    private String wireMockBaseUrl;


    ObjectMapper objectMapper = new ObjectMapper();



    @Test
    public void testGetPatientsDischargedWithinThreeDays() throws JsonProcessingException {

        // Create mock data
        Admission admission1 = new Admission();
        admission1.setId(1);
        admission1.setPatientID(1);
        admission1.setAdmissionDate(new Date(1640870400000L)); // Jan 1, 2022
        admission1.setDischargeDate(new Date(1641139600000L)); // Jan 3, 2022

        Admission admission2 = new Admission();
        admission2.setId(2);
        admission2.setPatientID(2);
        admission2.setAdmissionDate(new Date(1640966800000L)); // Jan 2, 2022
        admission2.setDischargeDate(new Date(1641139600000L)); // Jan 3, 2022

        Admission admission3 = new Admission();
        admission3.setId(3);
        admission3.setPatientID(3);
        admission3.setAdmissionDate(new Date(1652347200000L)); // May 11, 2022
        admission3.setDischargeDate(new Date(1652692800000L)); // May 15, 2022



        // Check if any admission was discharged within three days and create patient objects accordingly
        List<Patient> dischargedPatients = new ArrayList<>();
        if (admission1.getDischargeDate().getTime() - admission1.getAdmissionDate().getTime() <= 3 * 24 * 60 * 60 * 1000) {
            Patient patient1 = new Patient();
            patient1.setId(1);
            patient1.setForename("John");
            patient1.setSurname("Doe");
            patient1.setNhsNumber("1538022990");
            dischargedPatients.add(patient1);
        }
        if (admission2.getDischargeDate().getTime() - admission2.getAdmissionDate().getTime() <= 3 * 24 * 60 * 60 * 1000) {
            Patient patient2 = new Patient();
            patient2.setId(2);
            patient2.setForename("Jane");
            patient2.setSurname("Doe");
            patient2.setNhsNumber("1349876423");
            dischargedPatients.add(patient2);
        }
        if (admission3.getDischargeDate().getTime() - admission3.getAdmissionDate().getTime() <= 3 * 24 * 60 * 60 * 1000) {
            Patient patient3 = new Patient();
            patient3.setId(3);
            patient3.setForename("Joe");
            patient3.setSurname("Doe");
            patient3.setNhsNumber("3467097412");
            dischargedPatients.add(patient3);
        }

// Create stub mapping for the "/threedaydischarge" endpoint
        stubFor(get(urlEqualTo(wireMockBaseUrl +"/threedaydischarge"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(objectMapper.writeValueAsString(dischargedPatients))));



    }
}

