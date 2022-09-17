package pl.matbartc.librarian.storage.impl;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    final static Logger log = LoggerFactory.getLogger(DatabaseDocumentStorage.class);

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

        try {
            return documentsRepository.save(document);
        } catch (DataIntegrityViolationException e) {
            return handleDocumentSourceDuplication(url, e);
        }
    }

    private Document handleDocumentSourceDuplication(String offendingSource, DataIntegrityViolationException e) {
        if (e.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cve = ((ConstraintViolationException) e.getCause());
            if (cve.getConstraintName().contains("UK_SOURCE")) {
                return documentsRepository.findBySource(offendingSource);
            }
        }

        throw e;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public Document store(UUID documentId, DocumentData documentData) {
        final Document document = documentsRepository.findById(documentId).orElseThrow(() -> new UnknownDocumentException(documentId));

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
        final int updatedRows = documentsRepository.updateDocumentStatus(documentId, status);

        if (updatedRows == 0) {
            throw new UnknownDocumentException(documentId);
        }
    }

    @Override
    public DocumentStatus getStatus(UUID documentId) {
        Document d = fetch(documentId).orElseThrow(() -> new UnknownDocumentException(documentId));
        return d.getStatus();
    }
}
