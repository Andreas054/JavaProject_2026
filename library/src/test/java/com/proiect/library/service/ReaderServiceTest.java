package com.proiect.library.service;

import com.proiect.library.dto.ReaderDTO;
import com.proiect.library.exception.DuplicateResourceException;
import com.proiect.library.exception.ResourceNotFoundException;
import com.proiect.library.model.Reader;
import com.proiect.library.repository.LoanRepository;
import com.proiect.library.repository.ReaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReaderServiceTest {

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private ReaderService readerService;

    private Reader reader1;
    private Reader reader2;
    private ReaderDTO readerDTO;

    @BeforeEach
    void setUp() {
        reader1 = new Reader();
        reader1.setId(1L);
        reader1.setName("Reader1");
        reader1.setEmail("reader1@example.com");

        reader2 = new Reader();
        reader2.setId(2L);
        reader2.setName("Reader2");
        reader2.setEmail("reader2@example.com");

        readerDTO = new ReaderDTO(1L, "Reader1", "reader1@example.com", 0);
    }

    @Test
    void testGetAllReaders() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Reader> readers = Arrays.asList(reader1, reader2);
        Page<Reader> readerPage = new PageImpl<>(readers, pageable, readers.size());

        given(readerRepository.findAll(pageable)).willReturn(readerPage);
        given(loanRepository.findByReader_IdAndReturnDateIsNull(1L)).willReturn(new ArrayList<>());
        given(loanRepository.findByReader_IdAndReturnDateIsNull(2L)).willReturn(new ArrayList<>());

        Page<ReaderDTO> result = readerService.getAllReaders(pageable);

        assertEquals(2, result.getTotalElements());
        verify(readerRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetReaderById() {
        given(readerRepository.findById(1L)).willReturn(Optional.of(reader1));
        given(loanRepository.findByReader_IdAndReturnDateIsNull(1L)).willReturn(new ArrayList<>());

        ReaderDTO result = readerService.getReaderById(1L);

        assertNotNull(result);
        assertEquals("Reader1", result.getName());
    }

    @Test
    void testGetReaderById_NotFound() {
        given(readerRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> readerService.getReaderById(1L));
    }

    @Test
    void testCreateReader() {
        given(readerRepository.existsByEmail("reader1@example.com")).willReturn(false);
        given(readerRepository.save(any(Reader.class))).willReturn(reader1);
        given(loanRepository.findByReader_IdAndReturnDateIsNull(1L)).willReturn(new ArrayList<>());

        ReaderDTO result = readerService.createReader(readerDTO);

        assertNotNull(result);
        assertEquals("Reader1", result.getName());
        verify(readerRepository, times(1)).save(any(Reader.class));
    }

    @Test
    void testCreateReader_DuplicateEmail() {
        given(readerRepository.existsByEmail("reader1@example.com")).willReturn(true);

        assertThrows(DuplicateResourceException.class, () -> readerService.createReader(readerDTO));
    }

    @Test
    void testUpdateReader() {
        given(readerRepository.findById(1L)).willReturn(Optional.of(reader1));
        given(readerRepository.findByEmail("reader1@example.com")).willReturn(Optional.empty());
        given(readerRepository.save(any(Reader.class))).willReturn(reader1);
        given(loanRepository.findByReader_IdAndReturnDateIsNull(1L)).willReturn(new ArrayList<>());

        ReaderDTO result = readerService.updateReader(1L, readerDTO);

        assertNotNull(result);
        assertEquals("Reader1", result.getName());
        verify(readerRepository, times(1)).save(any(Reader.class));
    }

    @Test
    void testDeleteReader() {
        given(readerRepository.existsById(1L)).willReturn(true);
        doNothing().when(readerRepository).deleteById(1L);

        readerService.deleteReader(1L);

        verify(readerRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetReadersWithActiveLoans() {
        given(readerRepository.findAll()).willReturn(Arrays.asList(reader1, reader2));
        given(loanRepository.findByReader_IdAndReturnDateIsNull(1L)).willReturn(Arrays.asList(new com.proiect.library.model.Loan()));
        given(loanRepository.findByReader_IdAndReturnDateIsNull(2L)).willReturn(new ArrayList<>());

        List<ReaderDTO> result = readerService.getReadersWithActiveLoans();

        assertEquals(1, result.size());
        assertEquals("Reader1", result.get(0).getName());
    }
}
