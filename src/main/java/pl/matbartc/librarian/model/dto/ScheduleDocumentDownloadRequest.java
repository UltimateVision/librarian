package pl.matbartc.librarian.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ScheduleDocumentDownloadRequest implements Serializable {

    @NotBlank(message = "URL of document is required")
    private String url;

}
