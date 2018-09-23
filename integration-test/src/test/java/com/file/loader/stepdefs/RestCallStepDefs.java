package com.file.loader.stepdefs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sapsystem.AbstractIntegrationTest;
import com.sapsystem.api.rest.v1.dto.auth.AuthenticationRequest;
import com.sapsystem.api.rest.v1.dto.auth.AuthenticationResponse;
import com.sapsystem.security.pojo.JwtConfig;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class RestCallStepDefs extends AbstractIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(RestCallStepDefs.class);

    private final ObjectMapper mapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final JwtConfig jwtConfig;
    private MockHttpServletResponse response;
    private String jwtToken;

    protected RestCallStepDefs(MockMvc mockMvc, JwtConfig jwtConfig) {
        super(mockMvc);
        this.jwtConfig = jwtConfig;
    }

    @When("^user authenticate with password \"(.+)\"$")
    public void userAuthenticate(String password) throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(password);
        response = mockMvc.perform(requestBuilder("POST", "/v1/auth/", MediaType.APPLICATION_JSON_UTF8_VALUE,
                Collections.emptyMap(), mapper.writeValueAsString(authenticationRequest)))
                .andReturn()
                .getResponse();
        AuthenticationResponse authenticationResponse = mapper.readValue(response.getContentAsString(), AuthenticationResponse.class);
        jwtToken = authenticationResponse.getToken();
    }

    @When("^user sends (.*) request to endpoint (.*) with content-type (.*) and the following headers$")
    public void userSendsRequestWithHeader(String requestType, String endpointUrl,
                                           String contentType, Map<String, String> headers) throws Exception {
        response = mockMvc.perform(requestBuilder(requestType, endpointUrl, contentType, headers))
                .andReturn()
                .getResponse();
    }

    @When("^user sends (.*) request to endpoint (.*) with content-type (.*) and empty headers$")
    public void userSendsRequest(String requestType, String endpointUrl, String contentType) throws Exception {
        response = mockMvc.perform(requestBuilder(requestType, endpointUrl, contentType, Collections.emptyMap()))
                .andReturn()
                .getResponse();
    }

    @When("^user sends (.*) request to endpoint (.*) with content-type (.*) and empty headers and body$")
    public void userSendsRequestWithBody(String requestType, String endpointUrl, String contentType, DataTable dataTable) throws Exception {
        response = mockMvc.perform(requestBuilder(requestType, endpointUrl, contentType, Collections.emptyMap(), convertDataTableToJson(dataTable)))
                .andReturn()
                .getResponse();
    }

    @And("the \"([^\"]*)\" list size should be (\\d+)")
    public void verifyListSize(String keyPath, int listSize) throws IOException {
        String json = response.getContentAsString();
        JsonNode jsonNode = mapper.readTree(json);

        JsonNode node = find(keyPath, jsonNode);
        Assert.assertNotNull(node);
        Assert.assertEquals(listSize, node.size());
    }

    @Then("the response status code should be (\\d+)")
    public void verifyResponseStatusCode(int statusCode) {
        Assert.assertEquals(response.getStatus(), statusCode);
    }

    @And("^the json response has \"([^\"]*)\" object$")
    public void theJsonResponseHasDataObject(String keyPath) throws IOException {
        String json = response.getContentAsString();
        JsonNode jsonNode = mapper.readTree(json);
        Assert.assertNotNull("Node is null!", find(keyPath, jsonNode));
    }

    @And("^I should see \"(.+)\" json response with the following keys and values$")
    public void iShouldSeeJsonResponseWithTheFollowingKeysAndValues(String baseKeyPath,
                                                                    Map<String, String> responseFields) throws IOException {
        String json = response.getContentAsString();
        JsonNode jsonNode = mapper.readTree(json);

        JsonNode expectedNode = find(baseKeyPath, jsonNode);
        responseFields.forEach((key, value) -> {
            JsonNode valueNode = find(key, expectedNode);
            Assert.assertNotNull("Node is null", valueNode);
            Assert.assertEquals(value, valueNode.asText());
        });
    }

    @And("^I should see root json response with the following keys and values$")
    public void iShouldSeeJsonResponseWithTheFollowingKeysAndValues(Map<String, String> responseFields) throws IOException {
        String json = response.getContentAsString();
        JsonNode jsonNode = mapper.readTree(json);
        responseFields.forEach((key, value) -> {
            JsonNode valueNode = find(key, jsonNode);
            Assert.assertNotNull("Node is null", valueNode);
            Assert.assertEquals(value, valueNode.asText());
        });
    }

    private JsonNode find(String keyPath, JsonNode rootNode) {
        String[] keys = keyPath.split("\\.");
        JsonNode jsonNode = rootNode;
        for (String key : keys) {
            jsonNode = jsonNode.get(key);
            if (jsonNode == null) {
                return null;
            }
        }
        return jsonNode;
    }

    private RequestBuilder requestBuilder(String requestType, String endpointUrl,
                                          String contentType, Map<String, String> headers) {
        return requestBuilder(requestType, endpointUrl, contentType, headers, null);
    }

    private RequestBuilder requestBuilder(String requestType, String endpointUrl,
                                          String contentType, Map<String, String> headers,
                                          String body) {
        MockHttpServletRequestBuilder builder = requestBuilder(requestType, endpointUrl);
        builder.contentType(contentType);
        if (body != null) {
            builder.content(body);
        }
        if (jwtToken != null) {
            builder.header(AUTHORIZATION, jwtConfig.getPrefix() + jwtToken);
        }
        headers.forEach(builder::header);
        return builder;
    }

    private String convertDataTableToJson(DataTable dataTable) {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = dataTable.asMap(String.class, String.class);

        ObjectNode objectNode = objectMapper.createObjectNode();

        map.forEach((k, v) -> {
            try {
                JsonNode jsonNode = objectMapper.readTree(v);
                objectNode.set(k, jsonNode);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return objectNode.toString();
    }

    private MockHttpServletRequestBuilder requestBuilder(String requestType, String endpointUrl) {
        switch (requestType) {
            case "GET":
                return get(endpointUrl);
            case "POST":
                return post(endpointUrl);
            case "PUT":
                return put(endpointUrl);
            case "DELETE":
                return delete(endpointUrl);
            case "HEAD":
                return head(endpointUrl);
            case "PATCH":
                return patch(endpointUrl);
            case "OPTIONS":
                return options(endpointUrl);
            default:
                throw new IllegalArgumentException("Wrong request type: " + requestType);
        }
    }
}
