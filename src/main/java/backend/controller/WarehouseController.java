package backend.controller;

import backend.dto.WarehouseDto;
import backend.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
@Tag(name = "Склады", description = "Операции со складами")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping("/{id}")
    @Operation(summary = "Получить склад по ID")
    public ResponseEntity<WarehouseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getById(id));
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все склады")
    public ResponseEntity<List<WarehouseDto>> getAll() {
        return ResponseEntity.ok(warehouseService.getAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить склад по ID")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        warehouseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/isDeleted")
    @Operation(summary = "Обновить статус удаления склада")
    public ResponseEntity<WarehouseDto> isDeleted(@PathVariable Long id, @RequestParam boolean isDeleted) {
        return ResponseEntity.ok(warehouseService.isDeleted(id, isDeleted));
    }

    @PatchMapping("/{id}/{status}")
    @Operation(summary = "Изменить статус склада")
    public ResponseEntity<WarehouseDto> changeStatus(@PathVariable Long id, @PathVariable String status) {
        return ResponseEntity.ok(warehouseService.changeStatus(id, status));
    }

    @PostMapping("/save")
    @Operation(summary = "Сохранить склад")
    public ResponseEntity<WarehouseDto> saveWarehouse(@RequestBody WarehouseDto warehouseDto) {
        return ResponseEntity.ok(warehouseService.saveWarehouse(warehouseDto));
    }

}
