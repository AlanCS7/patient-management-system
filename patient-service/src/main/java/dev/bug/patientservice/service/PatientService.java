package dev.bug.patientservice.service;

import dev.bug.patientservice.dto.PatientRequestDTO;
import dev.bug.patientservice.dto.PatientResponseDTO;
import dev.bug.patientservice.exception.EmailAlreadyExistsException;
import dev.bug.patientservice.mapper.PatientMapper;
import dev.bug.patientservice.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
