package pl.matbartc.librarian.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScheduleDocumentDownloadRequest implements Serializable {

    private String url;

}
