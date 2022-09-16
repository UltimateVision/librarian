package pl.matbartc.librarian.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.matbartc.librarian.exceptions.UnknownDocumentException;
import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.model.DocumentStatus;
import pl.matbartc.librarian.storage.DocumentStorage;

import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private DocumentDownloader documentDownloader;

    @Autowired
    private DocumentStatusReporter documentStatusReporter;

    @Autowired
    private DocumentStorage storage;

    public Document scheduleDownload(String source) {
        Document document = storage.create(source);
        if (document.getStatus() == DocumentStatus.NEW) {
            documentDownloader.schedule(document);
        }
        return document;
    }

    public DocumentStatus getDocumentStatus(UUID documentId) {
        return documentStatusReporter.getStatus(documentId);
    }

    public Document getDocument(UUID documentId) {
        return storage.fetch(documentId).orElseThrow(() -> new UnknownDocumentException(documentId));
    }
}
