package com.example.app.downstream;

import com.example.api.exception.DownstreamServiceException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequiredArgsConstructor
public class ServiceClient {

    private final HttpClient httpClient;
    private final String url;

    public HttpResponse<String> get(String path) {
        final HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("%s%s".formatted(url, path)))
                .build();
        return sendRequest(request);
    }

    public HttpResponse<String> post(String path, String body) {
        final HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create("%s%s".formatted(url, path)))
                .build();
        return sendRequest(request);
    }

    public HttpResponse<String> delete(final String path) {
        final HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("%s%s".formatted(url, path)))
                .build();
        return sendRequest(request);
    }

    private HttpResponse<String> sendRequest(final HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new DownstreamServiceException("Failed to get response from downstream service, %s %s".formatted(request.method(), request.uri()), e);
        }
    }

}
