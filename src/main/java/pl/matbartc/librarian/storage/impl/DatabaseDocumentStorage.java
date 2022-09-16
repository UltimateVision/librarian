package pl.matbartc.librarian.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pl.matbartc.librarian.exceptions.UnknownDocumentException;
import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.model.DocumentData;
import pl.matbartc.librarian.model.DocumentStatus;
import pl.matbartc.librarian.service.DocumentStatusReporter;
import pl.matbartc.librarian.storage.DocumentStorage;
import pl.matbartc.librarian.storage.DocumentsRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class DatabaseDocumentStorage implements DocumentStorage, DocumentStatusReporter {

    @Autowired
    private DocumentsRepository documentsRepository;

    @Override
    public Document create(String url) {
        Document document = documentsRepository.findBySource(url);

        if (document != null) {
            return document;
        }

        document = new Document();
        document.setSource(url);

        return documentsRepository.save(document); // FIXME: exception on duplicated "source" entries
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public Document store(UUID documentId, DocumentData documentData) {
        final Document document = documentsRepository.findById(documentId).orElseThrow(() -> new UnknownDocumentException(documentId));

        document.setStatus(DocumentStatus.READY);
        document.setContent(documentData.getContent());
        document.setContentType(documentData.getContentType());

        return documentsRepository.save(document);
    }

    @Override
    public Optional<Document> fetch(UUID documentId) {
        return documentsRepository.findById(documentId);
    }

    @Override
    public void updateStatus(UUID documentId, DocumentStatus status) {
        documentsRepository.updateDocumentStatus(documentId, status);
    }

    @Override
    public DocumentStatus getStatus(UUID documentId) {
        Document d = fetch(documentId).orElseThrow(() -> new UnknownDocumentException(documentId));
        return d.getStatus();
    }
}
