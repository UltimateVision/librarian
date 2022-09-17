package pl.matbartc.librarian.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.matbartc.librarian.TestUtils;
import pl.matbartc.librarian.model.DocumentData;
import pl.matbartc.librarian.model.DocumentStatus;
import pl.matbartc.librarian.model.dto.ScheduleDocumentDownloadRequest;
import pl.matbartc.librarian.service.DownloadExecutor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DocumentsApiTest {

    private static final String API_URL = "http://localhost:8080/api/documents";
    private static final byte[] DOCUMENT_CONTENT = "Hello there!".getBytes();
    private static final long DOWNLOAD_DELAY = 500;

    @MockBean
    private DownloadExecutor downloadExecutor;

    @Test
    public void shouldReturnCreatedStatus_whenScheduleDocumentDownload() throws Exception {
        // Given
        final ScheduleDocumentDownloadRequest request = new ScheduleDocumentDownloadRequest();
        request.setUrl(TestUtils.generateRandomUrl());

        Mockito.when(downloadExecutor.download(Mockito.any())).thenReturn(new DocumentData(new byte[]{}, ""));

        // When
        final Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .post(API_URL);

        // Then
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        assertNotNull(response.jsonPath().getString("id"));
        assertEquals(DocumentStatus.NEW.toString(), response.jsonPath().getString("status"));
    }

    @Test
    public void shouldReturnUnprocessableEntity_whenPassingInvalidRequest() {
        // When
        final Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new ScheduleDocumentDownloadRequest())
                .post(API_URL);

        // Then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatusCode());
        assertNotNull(response.jsonPath().getString("url"));
    }

    @Test
    public void shouldReturnBadRequest_whenPassingEmptyRequest() {
        // When
        final Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(API_URL);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
    }

    @Test
    public void shouldReturnDocumentStatus() throws Exception {
        // Given
        final String documentId = scheduleDownloadWithoutDelay();

        // When
        final Response response = RestAssured.get(API_URL + "/status/" + documentId);

        // Then
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertNotNull(response.jsonPath().getString("documentStatus"));
    }

    @Test
    public void shouldReturnNotFound_whenAskingForStatusOfUnknownDocumentId() {
        // When
        final Response response = RestAssured.get(API_URL + "/status/" + UUID.randomUUID());

        // Then
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
    }

    @Test
    public void shouldReturnDocumentContent() throws Exception {
        // Given
        final String documentId = scheduleDownloadWithoutDelay();
        awaitReady(documentId);

        // When
        final Response response = RestAssured.get(API_URL + "/" + documentId);

        // Then
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertArrayEquals(DOCUMENT_CONTENT, response.getBody().asByteArray());
        assertEquals(MediaType.TEXT_PLAIN_VALUE, response.contentType());
    }

    @Test
    public void shouldReturnNotFound_whenAskingForUnknownDocument() {
        // When
        final Response response = RestAssured.get(API_URL + "/" + UUID.randomUUID());

        // Then
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
    }

    @Test
    public void shouldReturnNotFound_whenAskingForDocumentThatIsNotReady() throws Exception {
        // Given
        final String documentId = scheduleDownloadWithDelay();

        // When
        final Response response = RestAssured.get(API_URL + "/" + documentId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
    }

    private String scheduleDownloadWithoutDelay() throws Exception {
        Mockito.when(downloadExecutor.download(Mockito.any()))
                .thenReturn(new DocumentData(DOCUMENT_CONTENT, MediaType.TEXT_PLAIN_VALUE));

        return scheduleDownload();
    }

    private String scheduleDownloadWithDelay() throws Exception {
        Mockito.when(downloadExecutor.download(Mockito.any()))
                .then(document -> {
                    Thread.sleep(DOWNLOAD_DELAY);
                    return new DocumentData(DOCUMENT_CONTENT, MediaType.TEXT_PLAIN_VALUE);
                });

        return scheduleDownload();
    }

    private String scheduleDownload() {
        final ScheduleDocumentDownloadRequest request = new ScheduleDocumentDownloadRequest();
        request.setUrl(TestUtils.generateRandomUrl());

        final Response createDocumentResponse = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .post(API_URL);

        return createDocumentResponse.jsonPath().getString("id");
    }

    private void awaitReady(String documentId) throws InterruptedException {
        DocumentStatus status = null;
        while (status != DocumentStatus.READY) {
            Thread.sleep(10);
            Response r = RestAssured.get(API_URL + "/status/" + documentId);
            final String statusValue = r.jsonPath().getString("documentStatus");
            status = DocumentStatus.valueOf(statusValue);
        }
    }
}
