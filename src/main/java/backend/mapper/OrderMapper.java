package backend.mapper;

import backend.dto.CargoDto;
import backend.dto.CustomerDto;
import backend.dto.OrderDto;
import backend.enumeration.EntityType;
import backend.enumeration.OrderStatusEnum;
import backend.exception.ResourceNotFoundException;
import backend.feign.CustomerClient;
import backend.model.Order;
import backend.model.Warehouse;
import backend.repository.WarehouseRepository;
import backend.utils.ExternalIdGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final WarehouseRepository warehouseRepository;
    private final ExternalIdGenerator externalIdGenerator;
    private final CustomerClient customerClient;

    private Map<String, CustomerDto> cachedCustomersByEmail;

    // Преобразование из Entity в DTO
    public OrderDto convertToDto(Order entity) {
        return OrderDto.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .isDeleted(entity.getIsDeleted())
                .orderName(entity.getName())
                .notProcess(entity.getNotProcess())
                .status(entity.getStatus())
                .price(entity.getPrice())
                .warehouseId(entity.getWarehouse() != null ? entity.getWarehouse().getId() : null)
                .createdDate(entity.getCreatedDate())
                .createdBy(entity.getCreatedBy())
                .lastModifiedDate(entity.getLastModifiedDate())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }


    public OrderDto mapSingle(CargoDto cargo, Long warehouseId) {
        if (cachedCustomersByEmail == null) {
            cachedCustomersByEmail = customerClient.getAll().stream()
                    .filter(c -> c.getEmail() != null && !c.getEmail().isBlank())
                    .collect(Collectors.toMap(
                            c -> c.getEmail().trim().toLowerCase(Locale.ROOT),
                            Function.identity(),
                            (a, b) -> a
                    ));
        }

        String customerStr = Optional.ofNullable(cargo.getEmail())
                .map(e -> e.trim().toLowerCase(Locale.ROOT))
                .map(cachedCustomersByEmail::get)
                .map(c -> Stream.of(c.getFirstName(), c.getLastName(), c.getPhone(), c.getCity(), c.getEmail())
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.joining(" ")))
                .orElse(null);

        return OrderDto.builder()
                // id и externalId будут выставлены в сервисе (upsert-логика)
                .isDeleted(cargo.getIsDeleted())
                .orderName(cargo.getName())
                .notProcess(Boolean.FALSE)
                .status(OrderStatusEnum.PENDING)
                .price(cargo.getPrice())
                .warehouseId(warehouseId)
                .createdDate(cargo.getCreatedDate())
                .createdBy(cargo.getCreatedBy())
                .lastModifiedDate(cargo.getLastModifiedDate())
                .lastModifiedBy(cargo.getLastModifiedBy())
                .customer(customerStr)
                .build();
    }



    public List<OrderDto> mapAllWithGeneratedIds(List<CargoDto> cargos) {
        List<String> externalIds = externalIdGenerator.generateNextExternalIds(EntityType.ORDER, cargos.size());

        Map<String, CustomerDto> customersByEmail = customerClient.getAll().stream()
                .filter(c -> c.getEmail() != null && !c.getEmail().isBlank())
                .collect(Collectors.toMap(
                        c -> c.getEmail().trim().toLowerCase(Locale.ROOT),
                        Function.identity(),
                        (a, b) -> a
                ));
        List<OrderDto> orders = new ArrayList<>();

        for (int i = 0; i < cargos.size(); i++) {
            CargoDto cargo = cargos.get(i);
            String externalId = externalIds.get(i);

            Warehouse warehouse = warehouseRepository.findByWarehouseName(cargo.getWarehouseName())
                    .orElseThrow(() -> new EntityNotFoundException("Склад не найден: " + cargo.getWarehouseName()));

            var customerStr = resolveCustomerString(cargo, customersByEmail);


            OrderDto order = OrderDto.builder()
                    .externalId(externalId)
                    .isDeleted(cargo.getIsDeleted())
                    .orderName(cargo.getName())
                    .notProcess(Boolean.FALSE)
                    .status(OrderStatusEnum.PENDING)
                    .price(cargo.getPrice())
                    .warehouseId(warehouse.getId())
                    .createdDate(cargo.getCreatedDate())
                    .createdBy(cargo.getCreatedBy())
                    .lastModifiedDate(cargo.getLastModifiedDate())
                    .lastModifiedBy(cargo.getLastModifiedBy())
                    .customer(customerStr)
                    .build();

            orders.add(order);
        }

        return orders;
    }


    public List<Order> convertAllToEntities(List<OrderDto> dtos) {
        // Кэш по ID склада
        Map<Long, Warehouse> warehouseCache = new HashMap<>();

        return dtos.stream()
                .map(dto -> {
                    Warehouse warehouse = warehouseCache.computeIfAbsent(dto.getWarehouseId(), id ->
                            warehouseRepository.findById(id)
                                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id)));

                    return Order.builder()
                            .id(dto.getId())
                            .externalId(dto.getExternalId())
                            .isDeleted(dto.getIsDeleted())
                            .name(dto.getOrderName())
                            .notProcess(dto.getNotProcess())
                            .status(dto.getStatus())
                            .price(dto.getPrice())
                            .warehouse(warehouse)
                            .createdDate(dto.getCreatedDate())
                            .createdBy(dto.getCreatedBy())
                            .lastModifiedDate(dto.getLastModifiedDate())
                            .lastModifiedBy(dto.getLastModifiedBy())
                            .customer(dto.getCustomer())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Находит клиента по email из cargo (trim+lower) и собирает строку:
     * firstName + lastName + phone + city + email. Если клиента нет — возвращает null.
     */
    private String resolveCustomerString(CargoDto cargo, Map<String, CustomerDto> customersByEmail) {
        String email = cargo.getEmail();
        if (email == null || email.isBlank()) return null;

        CustomerDto c = customersByEmail.get(email.trim().toLowerCase(Locale.ROOT));
        if (c == null) return null;

        return Stream.of(c.getFirstName(), c.getLastName(), c.getPhone(), c.getCity(), c.getEmail())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" "));
    }
}
