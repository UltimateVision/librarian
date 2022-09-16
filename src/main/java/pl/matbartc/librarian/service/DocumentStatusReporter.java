package pl.matbartc.librarian.service;

import pl.matbartc.librarian.model.DocumentStatus;

import java.util.UUID;

public interface DocumentStatusReporter {

    void updateStatus(UUID documentId, DocumentStatus status);

    DocumentStatus getStatus(UUID documentId);
}
