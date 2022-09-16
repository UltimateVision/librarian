package pl.matbartc.librarian.exceptions;

import java.util.UUID;

public class DocumentNotReadyException extends RuntimeException {
    public DocumentNotReadyException(UUID documentId) {
        super(String.format("Document with id %s is not in READY state", documentId));
    }
}
