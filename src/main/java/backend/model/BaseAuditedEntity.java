package backend.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.time.Instant;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public abstract class BaseAuditedEntity extends BaseEntity {

    @Column(name = "created_date", columnDefinition = "TIMESTAMP", updatable = false)
    protected Instant createdDate;

    @Type(JsonType.class)
    @Column(name = "created_by", columnDefinition = "jsonb", updatable = false)
    protected UserInfo createdBy;

    @Column(name = "last_modified_date", columnDefinition = "TIMESTAMP")
    protected Instant lastModifiedDate;

    @Type(JsonType.class)
    @Column(name = "last_modified_by", columnDefinition = "jsonb")
    protected UserInfo lastModifiedBy;

}
