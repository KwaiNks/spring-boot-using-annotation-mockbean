package org.richmond.spring.web.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;

public class RestTemplateTests {

    private final ClientHttpRequestFactory requestFactory = mock(ClientHttpRequestFactory.class);

    private final ClientHttpRequest request = mock(ClientHttpRequest.class);

    private final ClientHttpResponse response = mock(ClientHttpResponse.class);
    private final ResponseErrorHandler errorHandler = mock(ResponseErrorHandler.class);

    private final HttpMessageConverter converter = mock(HttpMessageConverter.class);
    private final RestTemplate template = new RestTemplate(Collections.singletonList(converter));

    @BeforeEach
    void setup() {
        template.setRequestFactory(requestFactory);
        template.setErrorHandler(errorHandler);
    }

    // HTTP GET creating a Java entity

    @Test
    void getForEntity() throws Exception {

        // ARRANGE

        HttpHeaders requestHeaders = new HttpHeaders();
        mockSentRequest(GET, "https://example.com", requestHeaders);
        mockTextPlainHttpMessageConverter();
        mockResponseStatus(HttpStatus.OK);
        String expected = "Hello World";
        mockTextResponseBody(expected);

        // ACT
        ResponseEntity<String> result = template.getForEntity("https://example.com", String.class);

        // ASSERT
        assertThat(result.getBody()).isEqualTo(expected);
        assertThat(requestHeaders.getFirst("Accept")).isEqualTo(MediaType.TEXT_PLAIN_VALUE);
        assertThat(result.getHeaders().getContentType()).isEqualTo(MediaType.TEXT_PLAIN);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(response).close();
    }


    // HTTP Delete operation

    @Test
    void delete() throws Exception {
        // ARRANGE
        mockSentRequest(DELETE, "https://example.com");
        mockResponseStatus(HttpStatus.OK);

        // ACT
        template.delete("https://example.com");

        // ASSERT
        verify(response).close();
    }

    private void mockTextResponseBody(String expectedBody) throws Exception {
        mockResponseBody(expectedBody, MediaType.TEXT_PLAIN);
    }

    private void mockTextPlainHttpMessageConverter() {
        mockHttpMessageConverter(MediaType.TEXT_PLAIN, String.class);
    }

    private void mockHttpMessageConverter(MediaType mediaType, Class<?> type) {
        given(converter.canRead(type, null)).willReturn(true);
        given(converter.canRead(type, mediaType)).willReturn(true);
        given(converter.getSupportedMediaTypes()).willReturn(Collections.singletonList(mediaType));
        given(converter.canRead(type, mediaType)).willReturn(true);
        given(converter.canWrite(type, null)).willReturn(true);
        given(converter.canWrite(type, mediaType)).willReturn(true);
    }

    private void mockResponseBody(String expectedBody, MediaType mediaType) throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(mediaType);
        responseHeaders.setContentLength(expectedBody.length());
        given(response.getHeaders()).willReturn(responseHeaders);
        given(response.getBody()).willReturn(new ByteArrayInputStream(expectedBody.getBytes()));
        given(converter.read(eq(String.class), any(HttpInputMessage.class))).willReturn(expectedBody);
    }

    private void mockResponseStatus(HttpStatus responseStatus) throws Exception {
        given(request.execute()).willReturn(response);
        given(errorHandler.hasError(response)).willReturn(responseStatus.isError());
        given(response.getStatusCode()).willReturn(responseStatus);
        given(response.getRawStatusCode()).willReturn(responseStatus.value());
        given(response.getStatusText()).willReturn(responseStatus.getReasonPhrase());
    }

    private void mockSentRequest(HttpMethod method, String uri) throws Exception {
        mockSentRequest(method, uri, new HttpHeaders());
    }

    private void mockSentRequest(HttpMethod method, String uri, HttpHeaders requestHeaders) throws Exception {
        given(requestFactory.createRequest(new URI(uri), method)).willReturn(request);
        given(request.getHeaders()).willReturn(requestHeaders);
    }
}
