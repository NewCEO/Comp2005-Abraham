package com.example.maternityapi.FunctionalTests;

import com.example.maternityapi.Models.Admission;
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

import java.util.Date;

import static com.github.tomakehurst.wiremock.client.WireMock.*;



@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application.properties")
public class MostAdmissionsFunctionalTest {

    @Autowired
    private TestRestTemplate restTemplate;


    @Value("${wiremock.server.baseUrl}")
    private String wireMockBaseUrl;

    @Test
    public void testGetBusiestDayOfWeek() throws Exception{

        // Define the mock response for the admissions endpoint
        Admission admission1 = new Admission(1, new Date(1641530400000L), new Date(1641616800000L), 123);
        Admission admission2 = new Admission(2, new Date(1652563200000L), new Date(1652649600000L), 456);
        Admission[] admissions = {admission1, admission2};
        stubFor(get(urlPathEqualTo("/Admissions"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\":1,\"admissionDate\":\"2022-05-01\",\"dischargeDate\":\"2022-05-05\",\"patientId\":123},{\"id\":2,\"admissionDate\":\"2022-05-02\",\"dischargeDate\":\"2022-05-06\",\"patientId\":456}]")));


        // Send a GET request to the controller endpoint
        ResponseEntity<String> response = restTemplate.getForEntity(wireMockBaseUrl + "/busiest-day", String.class);

        // Verify that the response status is OK and the response body contains the expected message
//        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo("The busiest day of the week for admissions is Wednesday with 2 admissions.");
    }
}

