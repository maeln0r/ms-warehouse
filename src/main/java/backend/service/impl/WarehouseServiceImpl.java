package backend.service.impl;

import backend.dto.WarehouseDto;
import backend.enumeration.EntityType;
import backend.enumeration.WarehouseStatusEnum;
import backend.exception.ResourceNotFoundException;
import backend.mapper.WarehouseMapper;
import backend.model.Warehouse;
import backend.model.WarehouseGroup;
import backend.repository.WarehouseRepository;
import backend.service.WarehouseGroupService;
import backend.service.WarehouseService;
import backend.utils.ExternalIdGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository repository;
    private final WarehouseMapper warehouseMapper;
    private final ExternalIdGenerator externalIdGenerator;
    private final WarehouseGroupService warehouseGroupService;

    @Override
    @Transactional
    public WarehouseDto getById(Long id) {
        Warehouse warehouse = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        return warehouseMapper.convertToDto(warehouse);
    }

    @Override
    @Transactional
    public List<WarehouseDto> getAll() {
        return repository.findAll().stream()
                .map(warehouseMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        if (repository.existsById(id)) {
            throw new IllegalStateException("Нельзя удалить склад, пока есть связанные заказы, перенесите заказы на другой склад");
        }

        Warehouse warehouse = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        repository.delete(warehouse);
    }

    @Override
    @Transactional
    public WarehouseDto isDeleted(Long id, boolean isDeleted) {
        Warehouse warehouse = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        warehouse.setIsDeleted(isDeleted);
        warehouse.setLastModifiedDate(Instant.now());

        return warehouseMapper.convertToDto(repository.save(warehouse));
    }

    @Override
    @Transactional
    public WarehouseDto changeStatus(Long id, String status) {

        var warehouse = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        try {
            var newStatus = WarehouseStatusEnum.valueOf(status.toUpperCase());
            warehouse.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        warehouse.setLastModifiedDate(Instant.now());

        return warehouseMapper.convertToDto(repository.save(warehouse));
    }

    @Override
    @Transactional
    public Warehouse findById(Long warehouseId) {
        return repository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + warehouseId));
    }

    public Optional<Warehouse> getByName(String name) {
        return Optional.ofNullable(repository.findByWarehouseName(name).orElseThrow(() -> new ResourceNotFoundException("Склад с таким наименованием " + name + " не найден")));
    }


    @Override
    @Transactional
    public WarehouseDto saveWarehouse(WarehouseDto warehouseDto) {
        // Генерация нового externalId
        String newExternalId = externalIdGenerator.generateNextExternalId(EntityType.WAREHOUSE);

        WarehouseGroup warehouseGroup = warehouseGroupService.findById(warehouseDto.getWarehouseGroupId());


        // Создаём сущность склада
        Warehouse warehouse = Warehouse.builder()
                .externalId(newExternalId)
                .status(warehouseDto.getStatus() != null ? warehouseDto.getStatus() : WarehouseStatusEnum.ACTIVE)
                .warehouseName(warehouseDto.getWarehouseName())
                .isTransitWarehouse(warehouseDto.getIsTransitWarehouse())
                .warehouseGroup(warehouseGroup)
                .createdDate(Instant.now())
                .createdBy(warehouseDto.getCreatedBy())
                .lastModifiedDate(Instant.now())
                .lastModifiedBy(warehouseDto.getLastModifiedBy())
                .build();

        return warehouseMapper.convertToDto(repository.save(warehouse));
    }

}
