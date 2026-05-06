package com.proiect.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proiect.library.config.JwtAuthenticationFilter;
import com.proiect.library.config.SecurityConfig;
import com.proiect.library.dto.BookDTO;
import com.proiect.library.service.BookService;
import com.proiect.library.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private LogoutHandler logoutHandler;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetAllBooks() throws Exception {
        Page<BookDTO> page = new PageImpl<>(Collections.singletonList(new BookDTO()));
        given(bookService.getAllBooks(any(PageRequest.class))).willReturn(page);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateBook() throws Exception {
        Set<Long> authorIds = new HashSet<>();
        authorIds.add(1L);
        Set<Long> genreIds = new HashSet<>();
        genreIds.add(1L);

        BookDTO bookDTO = new BookDTO(null, "New Book", "1234567890", 2022, authorIds, genreIds, null, null, null);
        given(bookService.createBook(any(BookDTO.class))).willReturn(bookDTO);

        mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Book"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateBook() throws Exception {
        Set<Long> authorIds = new HashSet<>();
        authorIds.add(1L);
        Set<Long> genreIds = new HashSet<>();
        genreIds.add(1L);

        BookDTO bookDTO = new BookDTO(1L, "Updated Book", "1234567890", 2022, authorIds, genreIds, null, null, null);
        given(bookService.updateBook(eq(1L), any(BookDTO.class))).willReturn(bookDTO);

        mockMvc.perform(put("/api/books/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/books/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateBook_AsUser_Forbidden() throws Exception {
        Set<Long> authorIds = new HashSet<>();
        authorIds.add(1L);
        Set<Long> genreIds = new HashSet<>();
        genreIds.add(1L);

        BookDTO bookDTO = new BookDTO(null, "New Book", "1234567890", 2022, authorIds, genreIds, null, null, null);

        mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isForbidden());
    }
}
