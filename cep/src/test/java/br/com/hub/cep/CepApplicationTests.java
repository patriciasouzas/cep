package br.com.hub.cep;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.hub.cep.model.Address;
import br.com.hub.cep.service.CepService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpStatusCode;
import org.mockserver.model.MediaType;
import org.mockserver.springtest.MockServerTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;


@MockServerTest({"setup.origin.url=http://localhost:${mockServerPort}/ceps.csv"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class CepApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CepService service;
    private MockServerClient mockServer;

    @Test
    @Order(1)
    public void testGetZipCodeWhenNotReady() throws Exception {
        mockMvc.perform(get("/zipcode/3358150")).andExpect(status().is(503));
    }

    @Test
    @Order(2)
    public void testSetupOk() throws Exception {
        String cepContent = "SP,São Paulo,Vila Formosa,3358150,Rua Ituri,,,,,,,,,,";

        // Mock SETUP endpoint
        mockServer.when(request()
                        .withMethod("GET")
                        .withPath("/ceps.csv"))
                .respond(response()
                        .withStatusCode(HttpStatusCode.OK_200.code())
                        .withContentType(MediaType.PLAIN_TEXT_UTF_8)
                        .withBody(cepContent));

        this.service.setup();
    }

    @Test
    @Order(3)
    public void testGetZipCodeThatDoenstExist() throws Exception {
        mockMvc.perform(get("/zipcode/99999999")).andExpect(status().is(204));
    }

    @Test
    @Order(4)
    public void testGetZipCodeOk() throws Exception {

        MvcResult result = mockMvc.perform(get("/zipcode/03358150"))
                .andExpect(status().is(200))
                .andReturn();

        String addressResultJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

        String addressCorrectJson = new ObjectMapper().writeValueAsString(
                Address.builder()
                        .zipcode("03358150")
                        .street("Rua Ituri")
                        .city("São Paulo")
                        .state("SP")
                        .district("Vila Formosa")
                        .build());

        JSONAssert.assertEquals(addressCorrectJson, addressResultJson, false);
    }

}
