package pl.matbartc.librarian.model.dto;

import lombok.Data;
import pl.matbartc.librarian.model.DocumentStatus;

import java.io.Serializable;

@Data
public class DocumentStatusResponse implements Serializable {

    private DocumentStatus documentStatus;
}
