package pl.matbartc.librarian.model.dto;

import lombok.Data;
import pl.matbartc.librarian.model.DocumentStatus;

import java.io.Serializable;

@Data
public class ScheduleDocumentDownloadResponse implements Serializable {

    private String id;
    private DocumentStatus status;

}
