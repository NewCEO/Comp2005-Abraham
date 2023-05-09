package com.example.maternityapi.FunctionalTests;

import com.example.maternityapi.Models.Admission;
import com.example.maternityapi.Models.Allocation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
@AutoConfigureWireMock(port = 0,  files = "classpath:com/example/maternityapi/TestResource/Mockserver.Json")
@TestPropertySource(locations = "classpath:application.properties")
public class AverageStayFunctionalTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${wiremock.server.baseUrl}")
    private String wireMockBaseUrl;

    @Test
    public void testGetAverageStayDurationForStaff() throws Exception {

        // Define the mock response for the allocations endpoint
        Allocation allocation1 = new Allocation(1, 1, 123, new Date(1641530400000L), new Date(1641534000000L));
        Allocation[] allocations = {allocation1};
        stubFor(get(urlEqualTo("/COMP2005/api/allocations?employeeId=123"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(new ObjectMapper().writeValueAsString(allocations))));

        // Define the mock response for the admissions endpoint
        Admission admission1 = new Admission(1, new Date(1652563200000L), new Date(1653577600000L), 321);
        Admission[] admissions = {admission1};
        stubFor(get(urlEqualTo("/COMP2005/api/admissions?id=1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(new ObjectMapper().writeValueAsString(admissions))));

        // Send a GET request to the controller endpoint with employeeId=123
        ResponseEntity<String> response = restTemplate.getForEntity(wireMockBaseUrl + "/employee/123/avgduration", String.class);

        // Verify that the response status is OK and the response body contains the expected message
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("The average duration of stay for this employee patient is 31 days.");
    }
}

