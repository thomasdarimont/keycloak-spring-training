package training.apiapp.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminResourceTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Call endpoint /api/admin with user authority using GET")
    @WithMockUser
    void callAdminEndpointAsUserShouldFail() throws Exception {
        mvc.perform(get("/api/admin")) //
                .andExpect(status().isForbidden()) //
        ;
    }

    @Test
    @DisplayName("Call endpoint /api/admin with admin authority using GET")
    @WithMockUser(authorities = {"admin"})
    void callAdminEndpointAsAdminShouldPass() throws Exception {
        mvc.perform(get("/api/admin")) //
                .andExpect(status().isOk()) //
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //
                .andExpect(jsonPath("message").value("Admin user"));
    }
}