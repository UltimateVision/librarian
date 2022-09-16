package pl.matbartc.librarian.service.impl.executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.model.DocumentData;
import pl.matbartc.librarian.model.DocumentStatus;
import pl.matbartc.librarian.service.DocumentStatusReporter;
import pl.matbartc.librarian.storage.DocumentStorage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class JdkHttpDownloadExecutor extends DownloadExecutor {

    final static Logger log = LoggerFactory.getLogger(JdkHttpDownloadExecutor.class);

    final HttpClient httpClient = HttpClient.newHttpClient();

    public JdkHttpDownloadExecutor(DocumentStorage storage, DocumentStatusReporter statusReporter) {
        super(storage, statusReporter);
    }

    @Override
    public void download(Document document) throws Exception {
        statusReporter.updateStatus(document.getId(), DocumentStatus.DOWNLOADING);

        Thread.sleep(10000); // FIXME

        final UUID documentId = document.getId();

        HttpRequest request = HttpRequest.newBuilder(new URI(document.getSource())).GET().build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() == 200) {
            final String contentType = response.headers().firstValue(HttpHeaders.CONTENT_TYPE).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            final DocumentData documentData = new DocumentData(response.body(), contentType);

            storage.store(documentId, documentData);
        } else {
            log.error("Failed to download document {} - HTTP Status was {}", documentId, response.statusCode());
            statusReporter.updateStatus(documentId, DocumentStatus.ERROR);
        }
    }
}
