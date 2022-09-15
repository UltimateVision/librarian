package pl.matbartc.librarian.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.matbartc.librarian.model.Document;
import pl.matbartc.librarian.model.DocumentStatus;
import pl.matbartc.librarian.model.dto.DocumentDTO;
import pl.matbartc.librarian.model.dto.DownloadDocumentDTO;
import pl.matbartc.librarian.repo.DocumentsRepository;
import pl.matbartc.librarian.service.DocumentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentsController {

    Logger log = LoggerFactory.getLogger(DocumentsController.class);

    @Autowired
    DocumentService documentService;

    @PostMapping
    public DocumentDTO scheduleDownload(@RequestBody DownloadDocumentDTO downloadDocumentDTO) {
        Document document = documentService.scheduleDownload(downloadDocumentDTO.getUrl());

        DocumentDTO response = new DocumentDTO();

        response.setId(document.getId().toString());
        response.setStatus(document.getStatus());

        return response;
    }

    @GetMapping("/info/{id}")
    public DocumentStatus getDocumentInfo(@PathVariable String id) {
        return documentService.getDocumentStatus(UUID.fromString(id)).orElse(null); // FIXME: throw and handle exception
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getDocument(@PathVariable String id) {
        final Document document = documentService.getDocument(UUID.fromString(id)).orElseThrow(); // FIXME: throw

        if (document.getStatus() != DocumentStatus.READY) {
            // FIXME: throw
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .body(document.getData());
    }

}
