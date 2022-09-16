package pl.matbartc.librarian.storage;

import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.model.DocumentData;

import java.util.Optional;
import java.util.UUID;

public interface DocumentStorage {

    Document create(String url);

    Document store(UUID documentId, DocumentData document);

    Optional<Document> fetch(UUID documentId);
}
