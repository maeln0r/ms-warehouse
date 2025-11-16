package backend.dto;

import backend.enumeration.OrderStatusEnum;
import backend.model.UserInfo;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private String externalId;
    private Boolean isDeleted;
    private String orderName;
    private Boolean notProcess;
    private OrderStatusEnum status;
    private BigDecimal price;
    private Long warehouseId;
    private Instant createdDate;
    private UserInfo createdBy;
    private Instant lastModifiedDate;
    private UserInfo lastModifiedBy;
    private String customer;
}
