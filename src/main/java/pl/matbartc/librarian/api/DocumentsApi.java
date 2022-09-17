package pl.matbartc.librarian.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.matbartc.librarian.model.dto.DocumentStatusResponse;
import pl.matbartc.librarian.model.dto.ScheduleDocumentDownloadRequest;
import pl.matbartc.librarian.model.dto.ScheduleDocumentDownloadResponse;

import javax.validation.Valid;
import java.util.HashMap;

public interface DocumentsApi {
    @Operation(
            summary = "Schedules document download",
            description = "Upon completion returns created document ID that can be used to retrieve it's content." +
                    "If document with requested url has already been posted, the new download won't be scheduled. Instead, endpoint will return ID of " +
                    "existing document along with it's status.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Document created",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ScheduleDocumentDownloadResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid request body, see property - message map in body to examine what went wrong",
                    content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema
                    )
            }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request supplied",
                    content = @Content
            ),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ScheduleDocumentDownloadResponse scheduleDownload(@Valid @RequestBody ScheduleDocumentDownloadRequest scheduleDocumentDownloadRequest);

    @Operation(summary = "Gets status of the document")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Status retrieved",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DocumentStatusResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Document with corresponding ID does not exist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request supplied",
                    content = @Content
            ),
    })
    @GetMapping("/status/{id}")
    DocumentStatusResponse getDocumentStatus(@PathVariable String id);

    @Operation(
            summary = "Gets document content",
            description = "Serves content of document with provided ID. Retains original Content-Type."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Content retrieved",
                    content = {
                            @Content(
                                    mediaType = "*/*",
                                    schema = @Schema(type = "string", format = "byte")
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Document with corresponding ID does not exist OR is not available for download yet",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request supplied",
                    content = @Content
            ),
    })
    @GetMapping("/{id}")
    @ResponseBody
    ResponseEntity<byte[]> getDocument(@PathVariable String id);
}
