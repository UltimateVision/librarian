package pl.matbartc.librarian.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.matbartc.librarian.exceptions.DocumentNotReadyException;
import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.model.DocumentStatus;
import pl.matbartc.librarian.model.dto.ScheduleDocumentDownloadRequest;
import pl.matbartc.librarian.model.dto.ScheduleDocumentDownloadResponse;
import pl.matbartc.librarian.service.DocumentService;

import java.util.UUID;

/**
 * FIXME:
 * - API Docs
 * - Error handling
 * - Unit tests
 */
@RestController
@RequestMapping("/api/documents")
public class DocumentsController {

    @Autowired
    private DocumentService documentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleDocumentDownloadResponse scheduleDownload(@RequestBody ScheduleDocumentDownloadRequest scheduleDocumentDownloadRequest) {
        Document document = documentService.scheduleDownload(scheduleDocumentDownloadRequest.getUrl());

        ScheduleDocumentDownloadResponse response = new ScheduleDocumentDownloadResponse();

        response.setId(document.getId().toString());
        response.setStatus(document.getStatus());

        return response;
    }

    @GetMapping("/info/{id}")
    public DocumentStatus getDocumentInfo(@PathVariable String id) {
        return documentService.getDocumentStatus(UUID.fromString(id));
    }

    @GetMapping("/{id}")
    @ResponseBody
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
