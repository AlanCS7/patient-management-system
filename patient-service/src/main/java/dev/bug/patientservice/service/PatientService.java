package dev.bug.patientservice.service;

import dev.bug.patientservice.dto.PatientRequestDTO;
import dev.bug.patientservice.dto.PatientResponseDTO;
import dev.bug.patientservice.dto.PatientUpdateRequestDTO;
import dev.bug.patientservice.exception.EmailAlreadyExistsException;
import dev.bug.patientservice.exception.PatientNotFoundException;
import dev.bug.patientservice.mapper.PatientMapper;
import dev.bug.patientservice.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatients() {
        return patientRepository
                .findAll()
                .stream()
                .map(PatientMapper::toDTO)
                .toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.email())) {
            log.warn("Patient creation rejected because email already exists");
            throw new EmailAlreadyExistsException("Email address already exists");
        }

        var newPatient = PatientMapper.toModel(patientRequestDTO);
        var savedPatient = patientRepository.save(newPatient);
        return PatientMapper.toDTO(savedPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientUpdateRequestDTO patientUpdateRequestDTO) {
        var patient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Patient update rejected because patient with ID {} was not found", id);
                    return new PatientNotFoundException("Patient with ID %s was not found".formatted(id));
                });

        if (patientRepository.existsByEmailAndIdNot(patientUpdateRequestDTO.email(), id)) {
            log.warn("Patient update rejected because email already exists");
            throw new EmailAlreadyExistsException("Email address already exists");
        }

        PatientMapper.updateFromDTO(patient, patientUpdateRequestDTO);
        var updatedPatient = patientRepository.save(patient);

        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {
        var patient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Patient deletion rejected because patient with ID {} was not found", id);
                    return new PatientNotFoundException("Patient with ID %s was not found".formatted(id));
                });

        patientRepository.delete(patient);
    }
}
