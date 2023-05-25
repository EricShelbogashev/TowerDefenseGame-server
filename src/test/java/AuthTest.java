import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.nsu.shelbogashev.tdgserver.Application;
import ru.nsu.shelbogashev.tdgserver.api.API;
import ru.nsu.shelbogashev.tdgserver.api.v0.delegate.AuthApiDelegateImpl;
import ru.nsu.shelbogashev.tdgserver.server.dto.MessageDto;
import ru.nsu.shelbogashev.tdgserver.server.dto.ResponseFactory;
import ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(AuthApiDelegateImpl.class)
public class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void register_normal() throws Exception {
        String request = ApiHelper.toJson(ResponseFactory.toAuthDto("some_user", "some_password"));

        mockMvc.perform(MockMvcRequestBuilders.post(API.AUTH.REGISTER)
                        .contentType("application/json")
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    MessageDto messageDto = objectMapper.readValue(response.getContentAsString(), MessageDto.class);
                    assertEquals(messageDto.getMessage(), ResponseMessage.SUCCESSFUL_REGISTRATION);
                });
    }

    @Test
    public void register_bad_password() throws Exception {
    }

    @Test
    public void register_bad_username() throws Exception {
    }
}
