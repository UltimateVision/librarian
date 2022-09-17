package pl.matbartc.librarian.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.matbartc.librarian.TestUtils;
import pl.matbartc.librarian.exceptions.UnknownDocumentException;
import pl.matbartc.librarian.model.DocumentData;
import pl.matbartc.librarian.model.DocumentStatus;
import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.storage.impl.DatabaseDocumentStorage;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DatabaseDocumentStorageTest {

    @Autowired
    private DatabaseDocumentStorage storage;

    @Autowired
    private DocumentsRepository repository;
    
    @Test
    public void shouldCreateEmptyDocument() {
        // Given
        final String documentSource = TestUtils.generateRandomUrl();

        // When
        final Document document = storage.create(documentSource);

        // Then
        assertNotNull(document.getId());
        assertEquals(document.getSource(), documentSource);
        assertEquals(document.getStatus(), DocumentStatus.NEW);

        assertNotNull(document.getCreatedDate());
        assertNotNull(document.getModifiedDate());

        assertNull(document.getContentType());
        assertNull(document.getContent());
    }

    @Test
    public void shouldReturnExistingDocument_whenOneWithSameSourceExists() {
        // Given
        final String documentSource = TestUtils.generateRandomUrl();

        // When
        final Document document1 = storage.create(documentSource);
        final Document document2 = storage.create(documentSource);

        // Then
        assertEquals(document1.getId(), document2.getId());
        assertEquals(document1.getSource(), document2.getSource());
        assertEquals(document1.getStatus(), document2.getStatus());
        assertEquals(document1.getContentType(), document2.getContentType());
        assertArrayEquals(document1.getContent(),document2.getContent());

        Iterable<Document> i = repository.findAllById(Collections.singletonList(document1.getId()));

        assertEquals(1, StreamSupport.stream(i.spliterator(), false).count());
    }

    @Test
    public void shouldStoreDocumentData() {
        // Given
        final String documentSource = TestUtils.generateRandomUrl();
        final Document document = storage.create(documentSource);
        final DocumentData documentData = new DocumentData(documentSource.getBytes(), "plain/text");

        // When
        storage.store(document.getId(), documentData);

        // Then
        final Document storedDocument = repository.findById(document.getId()).orElse(null);

        assertNotNull(storedDocument);
        assertEquals(document.getId(), storedDocument.getId());
        assertEquals(documentData.getContentType(), storedDocument.getContentType());
        assertArrayEquals(documentData.getContent(), storedDocument.getContent());
        assertTrue(storedDocument.getModifiedDate().isAfter(document.getModifiedDate()));
    }

    @Test
    public void shouldThrowUnknownDocumentException_whenStoringDataForUnknownId() {
        // Given
        final String documentSource = TestUtils.generateRandomUrl();
        final DocumentData documentData = new DocumentData(documentSource.getBytes(), "plain/text");

        // When, then
        assertThrows(UnknownDocumentException.class, () -> storage.store(UUID.randomUUID(), documentData));
    }

    @Test
    public void shouldFetchExistingDocument() {
        // Given
        final String documentSource = TestUtils.generateRandomUrl();
        final Document document = storage.create(documentSource);

        // When
        final Document retrievedDocument = storage.fetch(document.getId()).orElse(null);

        // Then
        assertNotNull(retrievedDocument);

        assertEquals(document.getId(), retrievedDocument.getId());
        assertEquals(document.getSource(), retrievedDocument.getSource());
        assertEquals(document.getStatus(), retrievedDocument.getStatus());
        assertEquals(document.getContentType(), retrievedDocument.getContentType());
        assertArrayEquals(document.getContent(),retrievedDocument.getContent());
    }

    @Test
    public void shouldReturnEmptyOptional_whenFetchingDocumentForUnknownId() {
        assertTrue(storage.fetch(UUID.randomUUID()).isEmpty());
    }

    @Test
    public void shouldUpdateDocumentStatus() {
        // Given
        final String documentSource = TestUtils.generateRandomUrl();
        final Document document = storage.create(documentSource);

        // When
        storage.updateStatus(document.getId(), DocumentStatus.READY);

        // Then
        Document updatedDocument = repository.findById(document.getId()).orElse(null);

        assertNotNull(updatedDocument);
        assertNotEquals(document.getStatus(), updatedDocument.getStatus());
        assertEquals(DocumentStatus.READY, updatedDocument.getStatus());
    }

    @Test
    public void shouldThrowUnknownDocumentException_whenUpdatingStatusForUnknownDocumentId() {
        assertThrows(UnknownDocumentException.class, () -> storage.updateStatus(UUID.randomUUID(), DocumentStatus.READY));
    }

    @Test
    public void shouldFetchDocumentStatus() {
        // Given
        final String documentSource = TestUtils.generateRandomUrl();
        final Document document = storage.create(documentSource);

        // When
        final DocumentStatus status = storage.getStatus(document.getId());

        // Then
        assertNotNull(status);
        assertEquals(document.getStatus(), status);
    }

    @Test
    public void shouldThrowUnknownDocumentException_whenFetchingStatusForUnknownDocumentId() {
        assertThrows(UnknownDocumentException.class, () -> storage.getStatus(UUID.randomUUID()));
    }
}
