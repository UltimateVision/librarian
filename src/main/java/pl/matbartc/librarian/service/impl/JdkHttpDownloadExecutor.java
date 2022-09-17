package pl.matbartc.librarian.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import pl.matbartc.librarian.exceptions.DownloadFailureException;
import pl.matbartc.librarian.model.DocumentData;
import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.service.DownloadExecutor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
public class JdkHttpDownloadExecutor implements DownloadExecutor {

    public final static long DEFAULT_TIMEOUT_MILLIS = 10000;

    final HttpClient httpClient = HttpClient.newHttpClient();

    final private long timeout;

    public JdkHttpDownloadExecutor(@Value("${librarian.downloadTimeout:" + DEFAULT_TIMEOUT_MILLIS + "}") long timeout) {
        this.timeout = timeout;
    }

    @Override
    public DocumentData download(Document document) throws Exception {
        final UUID documentId = document.getId();

        HttpRequest request = HttpRequest.newBuilder(new URI(document.getSource()))
                .timeout(Duration.of(timeout, ChronoUnit.MILLIS))
                .GET()
                .build();

        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                final String contentType = response.headers()
                        .firstValue(HttpHeaders.CONTENT_TYPE)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

                return new DocumentData(response.body(), contentType);
            } else {
                throw new DownloadFailureException(String.format("Failed to download document %s - HTTP Status was %s", documentId, response.statusCode()));
            }
        } catch (HttpTimeoutException e) {
            throw new DownloadFailureException(String.format("Failed to download document %s - timeout", documentId));
        }
    }
}
