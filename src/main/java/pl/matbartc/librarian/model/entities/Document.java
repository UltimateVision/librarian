package pl.matbartc.librarian.model.entities;

import lombok.Data;
import org.hibernate.annotations.Type;
import pl.matbartc.librarian.model.DocumentStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UK_SOURCE", columnNames = {"source"})
})
public class Document {

    @Id
    @Column(name = "id", nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentStatus status = DocumentStatus.NEW;

    @Lob
    private byte[] content;

    private String contentType;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    @PrePersist
    public void onPrePersist() {
        final LocalDateTime now = LocalDateTime.now();
        setCreatedDate(now);
        setModifiedDate(now);
    }

    @PreUpdate
    public void onPreUpdate() {
        setModifiedDate(LocalDateTime.now());
    }
}
