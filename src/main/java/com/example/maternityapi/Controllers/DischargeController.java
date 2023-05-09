package com.example.maternityapi.Controllers;

import com.example.maternityapi.Models.Admission;
import com.example.maternityapi.Models.Patient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@RestController
public class DischargeController {

    private final RestTemplate restTemplate;
    private final String baseUrl = "https://web.socem.plymouth.ac.uk/COMP2005/api";

    public DischargeController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/threedaydischarge")
    public List<Patient> getPatientsDischargedWithinThreeDays() {
        List<Patient> dischargedPatients = new ArrayList<>();

        ResponseEntity<List<Admission>> admissionsResponse = restTemplate.exchange(
                baseUrl + "/admissions",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Admission>>(){});
        List<Admission> admissions = admissionsResponse.getBody();

        for (Admission admission : admissions) {
            Date dischargeDate = admission.getDischargeDate();
            Date admissionDate = admission.getAdmissionDate();
            if (admissionDate != null && dischargeDate != null) {
                long daysBetween = TimeUnit.MILLISECONDS.toDays(dischargeDate.getTime() - admissionDate.getTime());
                if (daysBetween <= 3) {
                    ResponseEntity<Patient> patientResponse = restTemplate.getForEntity(
                            baseUrl + "/patients/" + admission.getPatientID(),
                            Patient.class);
                    Patient patient = patientResponse.getBody();
                    dischargedPatients.add(patient);
                }
            }
        }

        return dischargedPatients;
    }

}




