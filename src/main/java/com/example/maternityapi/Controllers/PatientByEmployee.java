package com.example.maternityapi.Controllers;


import com.example.maternityapi.Models.Admission;
import com.example.maternityapi.Models.Allocation;
import com.example.maternityapi.Models.Patient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
public class PatientByEmployee {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/employees/{employeeId}/patientsByEmployeeID")
    public ResponseEntity<Patient[]> getPatientsByEmployee(@PathVariable int employeeId) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

        String allocationsUrl = "https://web.socem.plymouth.ac.uk/COMP2005/api/Allocations";
        ResponseEntity<String> allocationsResponse = restTemplate.exchange(
                allocationsUrl,
                HttpMethod.GET,
                entity,
                String.class
        );
        String allocationsResponseBody = allocationsResponse.getBody();
        System.out.println("Response Body: " + allocationsResponseBody);
        Allocation[] allocations = mapper.readValue(allocationsResponseBody, Allocation[].class);



//        Admission admission = null;

        for (Allocation allocation : allocations) {
            if (allocation.getEmployeeID() == employeeId) {
                String admissionsUrl = "https://web.socem.plymouth.ac.uk/COMP2005/api/Admissions";
                System.out.println("Request URL: " + admissionsUrl);
                ResponseEntity<String> admissionsResponse = restTemplate.exchange(
                        admissionsUrl,
                        HttpMethod.GET,
                        entity,
                        String.class
                );
                String admissionsResponseBody = admissionsResponse.getBody();
                System.out.println("Response Body: " + admissionsResponseBody);

                Admission[] admissions = mapper.readValue(admissionsResponseBody, Admission[].class);


                for (Admission ad : admissions) {
                    if (ad.getId() == allocation.getAdmissionID()) {
                        String patientsUrl = "https://web.socem.plymouth.ac.uk/COMP2005/api/Patients";
                        ResponseEntity<String> patientsResponse = restTemplate.exchange(
                                patientsUrl,
                                HttpMethod.GET,
                                entity,
                                String.class
                        );
                        String patientsResponseBody = patientsResponse.getBody();

                        Patient[] patients = mapper.readValue(patientsResponseBody, Patient[].class);

                        List<Patient> matchingPatients = new ArrayList<>();
                        for (Patient p : patients) {
                            if (p.getId() == ad.getPatientID()) {
                                matchingPatients.add(p);
                            }
                        }

                        Patient[] matchingPatientsArray = matchingPatients.toArray(new Patient[matchingPatients.size()]);
                        return ResponseEntity.ok(matchingPatientsArray);

                    }
                }


            }
        }
        return ResponseEntity.notFound().build();
    }
}








