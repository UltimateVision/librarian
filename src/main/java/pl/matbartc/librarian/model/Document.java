package pl.matbartc.librarian.model;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class Document {

    @Id
    @Column(name = "id", nullable = false)
    @Type(type="org.hibernate.type.UUIDCharType")
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String source;

    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentStatus status = DocumentStatus.NEW;

    @Lob
    private byte[] data;

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
