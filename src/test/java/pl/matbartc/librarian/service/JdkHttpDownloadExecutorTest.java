package pl.matbartc.librarian.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import pl.matbartc.librarian.exceptions.DownloadFailureException;
import pl.matbartc.librarian.model.DocumentData;
import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.service.impl.JdkHttpDownloadExecutor;

import static org.junit.jupiter.api.Assertions.*;

public class JdkHttpDownloadExecutorTest {

    private final JdkHttpDownloadExecutor downloadExecutor =  new JdkHttpDownloadExecutor(100);

    private MockWebServer mockWebServer;

    @BeforeEach
    public void init() {
        this.mockWebServer = new MockWebServer();
    }

    private Document getDocument() {
        final Document document = new Document();
        document.setSource(mockWebServer.url("/").toString());

        return document;
    }

    @Test
    public void shouldDownloadDocument() throws Exception {
        // Given
        final Document document = getDocument();
        final String responseBody = "hello";
        final MockResponse response = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                .setBody(responseBody)
                .setResponseCode(HttpStatus.OK.value());
        mockWebServer.enqueue(response);

        // When
        final DocumentData documentData = downloadExecutor.download(document);

        // Then
        assertArrayEquals(responseBody.getBytes(), documentData.getContent());
        assertEquals(MediaType.TEXT_PLAIN.toString(), documentData.getContentType());
    }

    @Test
    public void shouldThrowDownloadFailureException_whenResponseStatusIsNotOk() {
        // Given
        final Document document = getDocument();
        final MockResponse response = new MockResponse()
                .setResponseCode(404);
        mockWebServer.enqueue(response);

        // When, then
        assertThrows(DownloadFailureException.class, () -> downloadExecutor.download(document));
    }

    @Test
    public void shouldThrowDownloadFailureException_whenTimeoutOccurs() {
        // Given
        final Document document = getDocument();
        final MockResponse response = new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE);

        mockWebServer.enqueue(response);

        // When, then
        assertThrows(DownloadFailureException.class, () -> downloadExecutor.download(document));
    }
}
