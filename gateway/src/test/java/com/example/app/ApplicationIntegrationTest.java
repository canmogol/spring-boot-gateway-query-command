package com.example.app;

import com.example.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// We use @SpringBootTest to bootstrap the entire container.
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class
)
// We use @AutoConfigureMockMvc to enable and configure autoconfiguration of MockMvc, so that we have an instance of MockMvc.
@AutoConfigureMockMvc
// We use @TestPropertySource to configure the locations of properties files. The properties file will override the existing application.properties file.
@TestPropertySource(locations = "classpath:application-integration-test.properties")
class ApplicationIntegrationTest {

    // MockMvc is the main entry point for server-side Spring MVC test support.
    // Since Autowired fields are injected right after construction of a bean such a config field does not have to be public.
    @Autowired
    private MockMvc mockMvc;

    @Value("${test.admin.john.token}")
    private String adminJohnToken;

    @Value("${test.user.mike.token}")
    private String userMikeToken;

    @Test
    void should_returnStatus200_when_getUsersRequest() throws Exception {
        // GIVEN
        // The matcher should match the name of the first item to be bob.
        final ResultMatcher nameMatcher = jsonPath("$[0].name", is("John Doe"));
        final ResultMatcher usernameMatcher = jsonPath("$[0].username", is("user1"));
        final ResultMatcher rolesMatcher = jsonPath("$[0].roles[0]", is("user"));

        // WHEN
        // The request is a GET request to the /users endpoint.
        final MockHttpServletRequestBuilder getUsers = get("/users")
                .header("Authorization", "Bearer " + userMikeToken)
                .contentType(MediaType.APPLICATION_JSON);

        // THEN
        // The response is expected to be a JSON array.
        mockMvc.perform(getUsers)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(nameMatcher)
                .andExpect(usernameMatcher)
                .andExpect(rolesMatcher);
    }

}