package backend.repository;

import backend.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    boolean existsById(Long id);

    Optional<Warehouse> findTopByOrderByIdDesc();

    Optional<Warehouse> findByWarehouseName(String warehouseName);
}
