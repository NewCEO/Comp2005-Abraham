package com.example.maternityapi.Controllers;

import com.example.maternityapi.Models.Admission;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class MostAdmissions {

    private final RestTemplate restTemplate;
    private final String baseUrl = "https://web.socem.plymouth.ac.uk/COMP2005/api";

    public MostAdmissions(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/busiest-day")
    public ResponseEntity<String> getBusiestDayOfWeek() {
        // Retrieve all admissions
        Admission[] admissions = restTemplate.getForObject(baseUrl + "/Admissions", Admission[].class);

        // Count admissions for each day of the week
        Map<DayOfWeek, Long> dayOfWeekCountMap = Arrays.stream(admissions)
                .map(Admission::getAdmissionDate)
                .filter(Objects::nonNull)
                .map(Date::toInstant)
                .map(instant -> instant.atZone(ZoneId.systemDefault()).toLocalDate().getDayOfWeek())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // Find the busiest day of the week
        Optional<Map.Entry<DayOfWeek, Long>> busiestDayOfWeekOptional = dayOfWeekCountMap.entrySet().stream()
                .max(Map.Entry.comparingByValue());
        if (busiestDayOfWeekOptional.isPresent()) {
            Map.Entry<DayOfWeek, Long> busiestDayOfWeek = busiestDayOfWeekOptional.get();
            return ResponseEntity.ok("The busiest day of the week for admissions is " + busiestDayOfWeek.getKey().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " with " + busiestDayOfWeek.getValue() + " admissions.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

