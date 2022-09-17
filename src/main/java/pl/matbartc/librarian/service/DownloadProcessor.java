package pl.matbartc.librarian.service;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.matbartc.librarian.model.DocumentData;
import pl.matbartc.librarian.model.DocumentStatus;
import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.storage.DocumentStorage;

import java.util.UUID;

@Getter
public class DownloadProcessor {

    final static Logger log = LoggerFactory.getLogger(DownloadProcessor.class);

    final DocumentStorage storage;
    final DocumentStatusReporter documentStatusReporter;
    final DownloadExecutor downloadExecutor;

    public DownloadProcessor(
            DocumentStorage storage,
            DocumentStatusReporter documentStatusReporter,
            DownloadExecutor downloadExecutor
    ) {
        this.storage = storage;
        this.documentStatusReporter = documentStatusReporter;
        this.downloadExecutor = downloadExecutor;
    }

    public void process(Document document) {
        final UUID documentId = document.getId();

        documentStatusReporter.updateStatus(documentId, DocumentStatus.DOWNLOADING);

        try {
            final DocumentData documentData = downloadExecutor.download(document);

            storage.store(documentId, documentData);
            documentStatusReporter.updateStatus(documentId, DocumentStatus.READY);
        } catch (Exception e) {
            log.error("Download task failed due to", e);
            documentStatusReporter.updateStatus(documentId, DocumentStatus.ERROR);
        }
    }
}
