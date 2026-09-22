package dev.bug.patientservice.controller;

import dev.bug.patientservice.dto.PatientRequestDTO;
import dev.bug.patientservice.fixture.PatientFixture;
import dev.bug.patientservice.service.PatientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

@WebMvcTest(controllers = PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @Test
    @DisplayName("should get patients")
    void getPatients() throws Exception {
        var patients = List.of(
                PatientFixture.johnDoeResponse(),
                PatientFixture.janeDoeResponse());
        when(patientService.getPatients()).thenReturn(patients);

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(patients.get(0).id()))
                .andExpect(jsonPath("$[0].name").value(patients.get(0).name()))
                .andExpect(jsonPath("$[1].id").value(patients.get(1).id()))
                .andExpect(jsonPath("$[1].name").value(patients.get(1).name()));

        verify(patientService).getPatients();
    }

    @Test
    @DisplayName("should create patient")
    void createPatient() throws Exception {
        var requestBody = """
                {
                  "name": "John Doe",
                  "email": "john.doe@example.com",
                  "address": "123 Main St",
                  "dateOfBirth": "1999-10-26",
                  "registeredDate": "2025-05-10"
                }
                """;
        var request = new PatientRequestDTO(
                "John Doe",
                "john.doe@example.com",
                "123 Main St",
                LocalDate.of(1999, 10, 26),
                LocalDate.of(2025, 5, 10));
        var createdPatient = PatientFixture.createdPatientResponse();
        when(patientService.createPatient(request)).thenReturn(createdPatient);

        mockMvc.perform(post("/patients")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/patients/" + createdPatient.id()))
                .andExpect(jsonPath("$.id").value(createdPatient.id()))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.dateOfBirth").value("1999-10-26"));

        verify(patientService).createPatient(request);
    }

    @Test
    @DisplayName("should reject invalid patient request")
    void createPatientWithInvalidRequest() throws Exception {
        var requestBody = """
                {
                  "name": "",
                  "email": "invalid-email",
                  "address": "",
                  "dateOfBirth": "",
                  "registeredDate": null
                }
                """;

        mockMvc.perform(post("/patients")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed for one or more fields"))
                .andExpect(jsonPath("$.path").value("/patients"))
                .andExpect(jsonPath("$.errors.length()").value(5))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("Name is required"))
                .andExpect(jsonPath("$.errors[1].field").value("email"))
                .andExpect(jsonPath("$.errors[1].message").value("Email should be valid"));
    }
}
