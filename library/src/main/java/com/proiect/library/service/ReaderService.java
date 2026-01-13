package com.proiect.library.service;

import com.proiect.library.dto.ReaderDTO;
import com.proiect.library.exception.DuplicateResourceException;
import com.proiect.library.exception.ResourceNotFoundException;
import com.proiect.library.model.Reader;
import com.proiect.library.repository.LoanRepository;
import com.proiect.library.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReaderService {
    private final ReaderRepository readerRepository;
    private final LoanRepository loanRepository;

    public List<ReaderDTO> getAllReaders() {
        return readerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReaderDTO getReaderById(Long id) {
        Reader reader = readerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reader not found with id: " + id));
        return convertToDTO(reader);
    }

    public ReaderDTO createReader(ReaderDTO readerDTO) {
        // check duplicate email
        if (readerRepository.existsByEmail(readerDTO.getEmail())) {
            throw new DuplicateResourceException("Reader with email " + readerDTO.getEmail() + " already exists");
        }

        Reader reader = new Reader();
        reader.setName(readerDTO.getName());
        reader.setEmail(readerDTO.getEmail());

        Reader savedReader = readerRepository.save(reader);
        return convertToDTO(savedReader);
    }

    public ReaderDTO updateReader(Long id, ReaderDTO readerDTO) {
        Reader reader = readerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reader not found with id: " + id));

        // check duplicate email (excluding current reader)
        readerRepository.findByEmail(readerDTO.getEmail()).ifPresent(existingReader -> {
            if (!existingReader.getId().equals(id)) {
                throw new DuplicateResourceException("Reader with email " + readerDTO.getEmail() + " already exists");
            }
        });

        reader.setName(readerDTO.getName());
        reader.setEmail(readerDTO.getEmail());
        Reader updatedReader = readerRepository.save(reader);
        return convertToDTO(updatedReader);
    }

    public void deleteReader(Long id) {
        if (!readerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reader not found with id: " + id);
        }
        readerRepository.deleteById(id);
    }

    public List<ReaderDTO> getReadersWithActiveLoans() {
        return readerRepository.findAll().stream()
                .map(this::convertToDTO)
                .filter(dto -> dto.getActiveLoansCount() > 0)
                .collect(Collectors.toList());
    }

    private ReaderDTO convertToDTO(Reader reader) {
        ReaderDTO dto = new ReaderDTO();
        dto.setId(reader.getId());
        dto.setName(reader.getName());
        dto.setEmail(reader.getEmail());

        // count loans
        int activeLoans = loanRepository.findByReader_IdAndReturnDateIsNull(reader.getId()).size();
        dto.setActiveLoansCount(activeLoans);

        return dto;
    }
}

