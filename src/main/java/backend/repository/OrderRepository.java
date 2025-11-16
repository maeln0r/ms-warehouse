package backend.repository;

import backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o.externalId FROM Order o ORDER BY o.id DESC LIMIT 1")
    Optional<String> findLastExternalId();

    @Query("SELECT o.id FROM Order o WHERE o.warehouse.id = :warehouseId")
    List<Long> findOrderIdsByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Query("""
        select o from Order o
        where o.name in :names and o.warehouse.id in :warehouseIds
    """)
    List<Order> findByNameInAndWarehouseIdIn(@Param("names") Collection<String> names,
                                             @Param("warehouseIds") Collection<Long> warehouseIds);

}
