package pl.matbartc.librarian.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DownloadDocumentDTO implements Serializable {

    private String url;

}
