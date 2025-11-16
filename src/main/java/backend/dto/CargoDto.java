package backend.dto;

import backend.model.UserInfo;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CargoDto {
    private Long id;
    private String externalId;
    private Boolean isDeleted;
    private String name;
    private BigDecimal price;
    private String warehouseName;
    private Integer count;
    private Instant createdDate;
    private UserInfo createdBy;
    private Instant lastModifiedDate;
    private UserInfo lastModifiedBy;
    private String email;
}
