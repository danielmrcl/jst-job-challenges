package io.github.danielmrcl.desafiojst.controller;

import io.github.danielmrcl.desafiojst.model.dto.MensagemDTO;
import io.github.danielmrcl.desafiojst.service.LoginService;
import io.github.danielmrcl.desafiojst.utils.builder.LoginDTOBuilder;
import io.github.danielmrcl.desafiojst.utils.json.JsonConverterUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    private MockMvc mockMvc;

    private final String URLBASE = "/api/login";

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    void testLogarUsuarioQuandoHttpPOSTEntaoRetorneMensagemDTO() throws Exception {
        /* given */
        var loginDTO = LoginDTOBuilder.builder().build().toLoginDTO();
        var mensagemDTO = new MensagemDTO("token");

        /* when */
        Mockito.when(loginService.logarUsuario(loginDTO))
                .thenReturn(mensagemDTO);

        /* then */
        mockMvc.perform(MockMvcRequestBuilders.post(URLBASE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverterUtils.asJsonString(loginDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg", Matchers.is(mensagemDTO.getMsg())));
    }
}
