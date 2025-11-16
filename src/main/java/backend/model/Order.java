package backend.model;

import backend.enumeration.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
@SuperBuilder
@Table(name = "orders")
public class Order extends BaseAuditedEntity{

    @Column(name = "name")
    private String name;

    @Column(name = "not_process")
    private Boolean notProcess;

    @Builder.Default
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status = OrderStatusEnum.PENDING;

    @Column(name = "price")
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    private String customer;
}
