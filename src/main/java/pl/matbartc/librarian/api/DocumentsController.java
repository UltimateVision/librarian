package pl.matbartc.librarian.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.matbartc.librarian.exceptions.DocumentNotReadyException;
import pl.matbartc.librarian.model.DocumentStatus;
import pl.matbartc.librarian.model.dto.DocumentStatusResponse;
import pl.matbartc.librarian.model.dto.ScheduleDocumentDownloadRequest;
import pl.matbartc.librarian.model.dto.ScheduleDocumentDownloadResponse;
import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.service.DocumentService;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentsController implements DocumentsApi {

    @Autowired
    private DocumentService documentService;

    @Override
    public ScheduleDocumentDownloadResponse scheduleDownload(@Valid @RequestBody ScheduleDocumentDownloadRequest scheduleDocumentDownloadRequest) {
        Document document = documentService.scheduleDownload(scheduleDocumentDownloadRequest.getUrl());

        ScheduleDocumentDownloadResponse response = new ScheduleDocumentDownloadResponse();

        response.setId(document.getId().toString());
        response.setStatus(document.getStatus());

        return response;
    }

    @Override
    public DocumentStatusResponse getDocumentStatus(@PathVariable String id) {
        final DocumentStatus status = documentService.getDocumentStatus(UUID.fromString(id));

        final DocumentStatusResponse response = new DocumentStatusResponse();
        response.setDocumentStatus(status);

        return response;
    }

    @Override
    public ResponseEntity<byte[]> getDocument(@PathVariable String id) {
        final UUID documentId = UUID.fromString(id);
        final Document document = documentService.getDocument(UUID.fromString(id));

        if (document.getStatus() != DocumentStatus.READY) {
            throw new DocumentNotReadyException(documentId);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .body(document.getContent());
    }

}
