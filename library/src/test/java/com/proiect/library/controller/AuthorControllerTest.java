package com.proiect.library.controller;

import com.proiect.library.config.JwtAuthenticationFilter;
import com.proiect.library.config.SecurityConfig;
import com.proiect.library.dto.AuthorDTO;
import com.proiect.library.service.AuthorService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

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
    public void testGetAllAuthors() throws Exception {
        Page<AuthorDTO> page = new PageImpl<>(Collections.singletonList(new AuthorDTO(1L, "Author")));
        given(authorService.getAllAuthors(any(PageRequest.class))).willReturn(page);

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Author"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateAuthor() throws Exception {
        AuthorDTO authorDTO = new AuthorDTO(null, "New Author");
        given(authorService.createAuthor(any(AuthorDTO.class))).willReturn(new AuthorDTO(1L, "New Author"));

        mockMvc.perform(post("/api/authors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Author\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Author"));
    }
}
