package backend.service.impl;

import backend.dto.CargoDto;
import backend.dto.OrderDto;
import backend.enumeration.EntityType;
import backend.enumeration.OrderStatusEnum;
import backend.exception.ResourceNotFoundException;
import backend.mapper.OrderMapper;
import backend.model.Order;
import backend.model.Warehouse;
import backend.repository.OrderRepository;
import backend.service.OrderService;
import backend.service.WarehouseService;
import backend.utils.ExternalIdGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseService warehouseService;
    private final ExternalIdGenerator externalIdGenerator;

    @Override
    @Transactional
    public OrderDto getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return orderMapper.convertToDto(order);
    }

    @Override
    @Transactional
    public List<OrderDto> getAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        orderRepository.delete(order);
    }

    @Override
    @Transactional
    public OrderDto isDeleted(Long id, boolean isDeleted) {
        Order order = orderRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setIsDeleted(isDeleted);
        order.setLastModifiedDate(Instant.now());

        return orderMapper.convertToDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto changeStatus(Long id, String status) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        try {
            var newStatus = OrderStatusEnum.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        order.setLastModifiedDate(Instant.now());

        return orderMapper.convertToDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto updateOrderDetails(Long id, Boolean notProcess, Long warehouseId, BigDecimal price) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        Optional.ofNullable(notProcess).ifPresent(order::setNotProcess);

        Optional.ofNullable(warehouseId)
                .map(warehouseService::findById)
                .ifPresent(order::setWarehouse);

        Optional.ofNullable(price).ifPresent(order::setPrice);
        order.setLastModifiedDate(Instant.now());

        return orderMapper.convertToDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto saveOrder(OrderDto orderDto) {
        String newExternalId = externalIdGenerator.generateNextExternalId(EntityType.ORDER);

        Warehouse warehouse = warehouseService.findById(orderDto.getWarehouseId());

        Order order = Order.builder()
                .externalId(newExternalId)
                .isDeleted(false)
                .name(orderDto.getOrderName())
                .notProcess(orderDto.getNotProcess())
                .status(orderDto.getStatus() != null ? orderDto.getStatus() : OrderStatusEnum.PENDING)
                .price(orderDto.getPrice())
                .warehouse(warehouse)
                .createdDate(Instant.now())
                .createdBy(orderDto.getCreatedBy())
                .lastModifiedDate(Instant.now())
                .lastModifiedBy(orderDto.getLastModifiedBy())
                .build();

        return orderMapper.convertToDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public List<Long> getOrderIdsByWarehouse(Long warehouseId) {
        return orderRepository.findOrderIdsByWarehouseId(warehouseId);
    }


    @Override
    @Transactional
    public OrderDto transferOrderToAnotherWarehouse(Long orderId, Long newWarehouseId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ с ID " + orderId + " не найден"));

        Warehouse newWarehouse = warehouseService.findById(newWarehouseId);

        if (order.getWarehouse().getId().equals(newWarehouseId)) {
            throw new IllegalStateException("Заказ уже находится на складе с ID " + newWarehouseId);
        }

        order.setWarehouse(newWarehouse);
        order.setLastModifiedDate(Instant.now());

        return orderMapper.convertToDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public List<OrderDto> transferOrdersToAnotherWarehouse(List<Long> orderIds, Long newWarehouseId) {
        Warehouse newWarehouse = warehouseService.findById(newWarehouseId);

        List<Order> updatedOrders = orderRepository.findAllById(orderIds).stream()
                .map(order -> {
                    if (order.getWarehouse().getId().equals(newWarehouseId)) {
                        throw new IllegalStateException("Заказ с ID " + order.getId() + " уже находится на складе с ID " + newWarehouseId);
                    }
                    order.setWarehouse(newWarehouse);
                    order.setLastModifiedDate(Instant.now());
                    return order;
                })
                .collect(Collectors.toList());

        orderRepository.saveAll(updatedOrders);

        return updatedOrders.stream()
                .map(orderMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void saveFromCargos(List<CargoDto> cargos) {
        // 1) вычисляем целевые warehouseId для всех cargo (один раз)
        Map<String, Long> warehouseIdsByName = cargos.stream()
                .map(CargoDto::getWarehouseName)
                .distinct()
                .collect(Collectors.toMap(
                        wn -> wn,
                        wn -> warehouseService.getByName(wn).get().getId()
                ));

        // 2) ключ = name + '#' + warehouseId
        Function<CargoDto, String> keyFn = c ->
                c.getName() + "#" + warehouseIdsByName.get(c.getWarehouseName());

        // 3) подгружаем уже существующие заказы по множеству имён и складов
        List<String> names = cargos.stream().map(CargoDto::getName).distinct().toList();
        List<Long> whIds = new ArrayList<>(new HashSet<>(warehouseIdsByName.values()));
        Map<String, Order> existingByKey = orderRepository
                .findByNameInAndWarehouseIdIn(names, whIds).stream()
                .collect(Collectors.toMap(
                        o -> o.getName() + "#" + o.getWarehouse().getId(),
                        Function.identity(),
                        (a, b) -> a
                ));

        // 4) считаем, сколько НОВЫХ (нет ключа в БД)
        List<CargoDto> newCargos = cargos.stream()
                .filter(c -> !existingByKey.containsKey(keyFn.apply(c)))
                .toList();

        // 5) генерим externalId ТОЛЬКО для новых
        Iterator<String> newIds = externalIdGenerator
                .generateNextExternalIds(EntityType.ORDER, newCargos.size())
                .iterator();

        // 6) строим OrderDto: для существующих — проставляем id и externalId из БД; для новых — берём из генератора
        List<OrderDto> orderDtos = cargos.stream()
                .map(c -> {
                    String key = keyFn.apply(c);
                    Order existing = existingByKey.get(key);

                    OrderDto dto = orderMapper.mapSingle(c,    // маппинг всех полей, customer и т.д.
                            warehouseIdsByName.get(c.getWarehouseName()));

                    if (existing != null) {
                        // обновление: сохраняем id + externalId существующей записи
                        dto.setId(existing.getId());
                        dto.setExternalId(existing.getExternalId());
                    } else {
                        // создание: назначаем следующий externalId
                        dto.setExternalId(newIds.next());
                    }
                    return dto;
                })
                .toList();

        // 7) в сущности и saveAll — JPA обновит по id, создаст без id
        List<Order> entities = orderMapper.convertAllToEntities(orderDtos);
        orderRepository.saveAll(entities);
    }
}