package com.example.maternityapi.Controllers;

import com.example.maternityapi.Models.Admission;
import com.example.maternityapi.Models.Allocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class AverageStay {


    @Autowired
    private final RestTemplate restTemplate;


    private static final String BASE_URL = "https://web.socem.plymouth.ac.uk/COMP2005/api";

    public AverageStay(RestTemplate restTemplate) {

        this.restTemplate = restTemplate;

    }

    @GetMapping("/employee/{employeeId}/avgduration")
    public ResponseEntity<String> getAverageStayDurationForStaff(@PathVariable int employeeId) {
        // Retrieve all allocations for the specified employee
        String allocationsUrl = BASE_URL + "/allocations?employeeId=" + employeeId;
        ResponseEntity<Allocation[]> allocationsResponse = restTemplate.getForEntity(allocationsUrl, Allocation[].class);

        if (allocationsResponse.getStatusCode() == HttpStatus.OK) {
            Allocation[] allocations = allocationsResponse.getBody();
            if (allocations.length == 0) {
                // Return a message if there are no allocations for the specified staff member
                return ResponseEntity.ok("No allocations found for this staff member.");
            }

            // Retrieve admissions for the specified employee and calculate the average duration of stay
            List<Long> durations = new ArrayList<>();
            for (Allocation allocation : allocations) {
                String admissionsUrl = BASE_URL + "/admissions?id=" + allocation.getAdmissionID();
                ResponseEntity<Admission[]> admissionsResponse = restTemplate.getForEntity(admissionsUrl, Admission[].class);

                if (admissionsResponse.getStatusCode() == HttpStatus.OK) {
                    Admission[] admissions = admissionsResponse.getBody();

                    for (Admission admission : admissions) {
                        try {
                            Date admissionDate = admission.getAdmissionDate();
                            Date dischargeDate = admission.getDischargeDate();
                            if (admissionDate == null || dischargeDate == null) {
                                // Skip the admission record if admission date or discharge date is null
                                continue;
                            }
                            long duration = (dischargeDate.getTime() - admissionDate.getTime()) / (1000 * 60 * 60 * 24);
                            durations.add(duration);
                        } catch (NullPointerException | IllegalArgumentException e) {
                            // Skip the admission record if there's an invalid admission date or discharge date
                            continue;
                        }
                    }
                }
            }

            if (durations.isEmpty()) {
                // Return a message if there are no admissions for the specified staff member
                return ResponseEntity.ok("No admissions found for this staff member.");
            }

            // Calculate the average duration of stay for the specified staff member
            long totalDuration = 0;
            for (long duration : durations) {
                totalDuration += duration;
            }
            long averageDuration = totalDuration / durations.size();

            // Return the average duration of stay as a string
            return ResponseEntity.ok("The average duration of stay for this employee patient is " + averageDuration + " days.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
