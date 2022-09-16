package pl.matbartc.librarian.exceptions;

import java.util.UUID;

public class UnknownDocumentException extends RuntimeException {

    public UnknownDocumentException(UUID documentId) {
        super(String.format("Document with id %s does not exist", documentId));
    }
}
