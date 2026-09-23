package dev.bug.patientservice.controller;

import dev.bug.patientservice.dto.PatientRequestDTO;
import dev.bug.patientservice.exception.EmailAlreadyExistsException;
import dev.bug.patientservice.exception.PatientNotFoundException;
import dev.bug.patientservice.fixture.PatientFixture;
import dev.bug.patientservice.service.PatientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andExpect(jsonPath("$.errors[?(@.field == 'name')].message", hasItem("Name is required")))
                .andExpect(jsonPath("$.errors[?(@.field == 'email')].message", hasItem("Email should be valid")));
    }

    @Test
    @DisplayName("should return conflict when creating a patient with an existing email")
    void createPatientWhenEmailAlreadyExists() throws Exception {
        var requestBody = """
                {
                  "name": "John Doe",
                  "email": "john.doe@example.com",
                  "address": "123 Main St",
                  "dateOfBirth": "1999-10-26",
                  "registeredDate": "2025-05-10"
                }
                """;
        when(patientService.createPatient(new PatientRequestDTO(
                "John Doe",
                "john.doe@example.com",
                "123 Main St",
                LocalDate.of(1999, 10, 26),
                LocalDate.of(2025, 5, 10))))
                .thenThrow(new EmailAlreadyExistsException("Email address already exists"));

        mockMvc.perform(post("/patients")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email address already exists"));
    }

    @Test
    @DisplayName("should update patient")
    void updatePatient() throws Exception {
        var id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        var requestBody = """
                {
                  "name": "John Doe Jr",
                  "email": "john.doe.jr@example.com",
                  "address": "901 Oak Ave",
                  "dateOfBirth": "1999-10-26"
                }
                """;
        var request = PatientFixture.createPatientUpdateRequestDTO();
        var updatedPatient = PatientFixture.createdPatientResponse();
        when(patientService.updatePatient(id, request)).thenReturn(updatedPatient);

        mockMvc.perform(put("/patients/{id}", id)
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedPatient.id()))
                .andExpect(jsonPath("$.name").value(updatedPatient.name()));

        verify(patientService).updatePatient(id, request);
    }

    @Test
    @DisplayName("should reject invalid patient update request")
    void updatePatientWithInvalidRequest() throws Exception {
        var id = UUID.randomUUID();
        var requestBody = """
                {
                  "name": "",
                  "email": "invalid-email",
                  "address": "",
                  "dateOfBirth": null
                }
                """;

        mockMvc.perform(put("/patients/{id}", id)
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed for one or more fields"))
                .andExpect(jsonPath("$.path").value("/patients/" + id))
                .andExpect(jsonPath("$.errors.length()").value(4));
    }

    @Test
    @DisplayName("should return not found when updating an unknown patient")
    void updatePatientWhenPatientDoesNotExist() throws Exception {
        var id = UUID.randomUUID();
        var request = PatientFixture.createPatientUpdateRequestDTO();
        var requestBody = """
                {
                  "name": "John Doe Jr",
                  "email": "john.doe.jr@example.com",
                  "address": "901 Oak Ave",
                  "dateOfBirth": "1999-10-26"
                }
                """;
        when(patientService.updatePatient(id, request))
                .thenThrow(new PatientNotFoundException("Patient with ID %s was not found".formatted(id)));

        mockMvc.perform(put("/patients/{id}", id)
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient with ID " + id + " was not found"));
    }

    @Test
    @DisplayName("should return conflict when updating with an existing email")
    void updatePatientWhenEmailAlreadyExists() throws Exception {
        var id = UUID.randomUUID();
        var request = PatientFixture.createPatientUpdateRequestDTO();
        var requestBody = """
                {
                  "name": "John Doe Jr",
                  "email": "john.doe.jr@example.com",
                  "address": "901 Oak Ave",
                  "dateOfBirth": "1999-10-26"
                }
                """;
        when(patientService.updatePatient(id, request))
                .thenThrow(new EmailAlreadyExistsException("Email address already exists"));

        mockMvc.perform(put("/patients/{id}", id)
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email address already exists"));
    }

    @Test
    @DisplayName("should delete patient")
    void deletePatient() throws Exception {
        var id = UUID.randomUUID();

        mockMvc.perform(delete("/patients/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(status().is(204));

        verify(patientService).deletePatient(id);
    }

    @Test
    @DisplayName("should return not found when deleting an unknown patient")
    void deletePatientWhenPatientDoesNotExist() throws Exception {
        var id = UUID.randomUUID();
        doThrow(new PatientNotFoundException("Patient with ID %s was not found".formatted(id)))
                .when(patientService)
                .deletePatient(id);

        mockMvc.perform(delete("/patients/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient with ID " + id + " was not found"));
    }
}
