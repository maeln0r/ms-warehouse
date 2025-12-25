package backend.controller;

import backend.dto.OrderDto;
import backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Tag(name = "Заказы", description = "Операции с заказами")
public class OrderController {

    private final OrderService service;

    @GetMapping("/{id}")
    @Operation(summary = "Получить заказ по ID")
    public ResponseEntity<OrderDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все заказы")
    public ResponseEntity<List<OrderDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить заказ по ID")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/isDeleted")
    @Operation(summary = "Изменить флаг isDeleted для заказа")
    public ResponseEntity<OrderDto> isDeleted(@PathVariable Long id, @RequestParam boolean isDeleted) {
        return ResponseEntity.ok(service.isDeleted(id, isDeleted));
    }

    @PatchMapping("/{id}/{status}")
    @Operation(summary = "Изменить статус заказа")
    public ResponseEntity<OrderDto> changeStatus(@PathVariable Long id, @PathVariable String status) {
        return ResponseEntity.ok(service.changeStatus(id, status));
    }

    @PatchMapping("/{id}/update")
    @Operation(summary = "Обновить детали заказа (notProcess, warehouseId, price)")
    public ResponseEntity<OrderDto> updateOrderDetails(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean notProcess,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) BigDecimal price) {
        return ResponseEntity.ok(service.updateOrderDetails(id, notProcess, warehouseId, price));
    }

    @PostMapping("/save")
    @Operation(summary = "Сохранить новый заказ")
    public ResponseEntity<OrderDto> saveOrder(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(service.saveOrder(orderDto));
    }

    @GetMapping("/warehouse/{warehouseId}/order")
    @Operation(summary = "Получить ID заказов по складу")
    public ResponseEntity<List<Long>> getOrderIdsByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(service.getOrderIdsByWarehouse(warehouseId));
    }

    @PatchMapping("/{id}/transfer/{newWarehouseId}")
    @Operation(summary = "Перенести заказ на другой склад")
    public ResponseEntity<OrderDto> transferOrderToAnotherWarehouse(
            @PathVariable Long id,
            @PathVariable Long newWarehouseId) {
        return ResponseEntity.ok(service.transferOrderToAnotherWarehouse(id, newWarehouseId));
    }

    @PatchMapping("/transfer")
    @Operation(summary = "Перенести несколько заказов на другой склад")
    public ResponseEntity<List<OrderDto>> transferOrdersToAnotherWarehouse(
            @RequestParam List<Long> orderIds,
            @RequestParam Long newWarehouseId) {
        return ResponseEntity.ok(service.transferOrdersToAnotherWarehouse(orderIds, newWarehouseId));
    }

}
