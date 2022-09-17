package pl.matbartc.librarian.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.matbartc.librarian.model.DocumentData;
import pl.matbartc.librarian.model.DocumentStatus;
import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.storage.DocumentStorage;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DownloadProcessorTest {

    @Mock
    DocumentStorage storage;

    @Mock
    DocumentStatusReporter statusReporter;

    @Mock
    DownloadExecutor downloadExecutor;

    private DownloadProcessor getProcessor() {
        return new DownloadProcessor(storage, statusReporter, downloadExecutor);
    }

    @Test
    public void shouldDownloadDocumentFromPoll() throws Exception {
        // Given
        final Document document = new Document();
        final DocumentData documentData = new DocumentData(new byte[]{}, "binary");

        when(storage.store(document.getId(), documentData)).thenReturn(new Document());
        when(downloadExecutor.download(document)).thenReturn(documentData);

        // When
        getProcessor().process(document);

        // Then
        verify(storage, times(1)).store(document.getId(), documentData);
        verify(downloadExecutor, times(1)).download(document);

        InOrder inOrder = inOrder(statusReporter, storage);
        inOrder.verify(statusReporter).updateStatus(document.getId(), DocumentStatus.DOWNLOADING);
        inOrder.verify(storage).store(document.getId(), documentData);
        inOrder.verify(statusReporter).updateStatus(document.getId(), DocumentStatus.READY);

        verify(statusReporter, never()).updateStatus(document.getId(), DocumentStatus.ERROR);
    }

    @Test
    public void shouldSetDocumentStatusToError_whenDownloadFails() throws Exception {
        // Given
        final Document document = new Document();
        when(downloadExecutor.download(document)).thenThrow(new Exception());

        // When
        getProcessor().process(document);

        // Then
        verifyNoInteractions(storage);
        verify(downloadExecutor, times(1)).download(document);

        InOrder inOrder = inOrder(statusReporter);
        inOrder.verify(statusReporter).updateStatus(document.getId(), DocumentStatus.DOWNLOADING);
        inOrder.verify(statusReporter).updateStatus(document.getId(), DocumentStatus.ERROR);

        verify(statusReporter, never()).updateStatus(document.getId(), DocumentStatus.READY);
    }

    @Test
    public void shouldSetDocumentStatusToError_whenStoringFails() throws Exception {
        // Given
        final Document document = new Document();
        final DocumentData documentData = new DocumentData(new byte[]{}, "binary");

        when(downloadExecutor.download(document)).thenReturn(documentData);
        when(storage.store(document.getId(), documentData)).thenThrow(new RuntimeException());

        // When
        getProcessor().process(document);

        // Then
        verify(downloadExecutor, times(1)).download(document);
        verify(storage, times(1)).store(document.getId(), documentData);

        InOrder inOrder = inOrder(statusReporter);
        inOrder.verify(statusReporter).updateStatus(document.getId(), DocumentStatus.DOWNLOADING);
        inOrder.verify(statusReporter).updateStatus(document.getId(), DocumentStatus.ERROR);

        verify(statusReporter, never()).updateStatus(document.getId(), DocumentStatus.READY);
    }


}
