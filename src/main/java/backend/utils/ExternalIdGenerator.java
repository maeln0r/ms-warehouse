package backend.utils;

import backend.enumeration.EntityType;
import backend.repository.OrderRepository;
import backend.repository.WarehouseRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class ExternalIdGenerator {

    private final OrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;

    public String generateNextExternalId(EntityType entityType) {
        Optional<String> lastExternalId;

        if (entityType == EntityType.ORDER) {
            lastExternalId = orderRepository.findLastExternalId();
        } else if (entityType == EntityType.WAREHOUSE) {
            lastExternalId = warehouseRepository.findTopByOrderByIdDesc();
        } else {
            throw new IllegalArgumentException("Unsupported entity type for external ID generation");
        }

        String prefix = entityType.getPrefix();

        int nextNumber = lastExternalId
                .map(id -> {
                    String numericPart = id.replace(prefix, ""); // Убираем префикс
                    int number = Integer.parseInt(numericPart); // Преобразуем в число
                    return number + 1; // Инкрементируем
                })
                .orElse(1); // Если записей нет, начинаем с 1

        return prefix + (prefix.equals("W-") ? String.format("%03d", nextNumber) : nextNumber);

    }


    public List<String> generateNextExternalIds(EntityType entityType, int count) {
        Optional<String> lastExternalId;

        if (entityType == EntityType.ORDER) {
            lastExternalId = orderRepository.findLastExternalId();
        } else if (entityType == EntityType.WAREHOUSE) {
            lastExternalId = warehouseRepository.findTopByOrderByIdDesc();
        } else {
            throw new IllegalArgumentException("Unsupported entity type for external ID generation");
        }

        String prefix = entityType.getPrefix();

        int start = lastExternalId
                .map(id -> Integer.parseInt(id.replace(prefix, "")))
                .orElse(0) + 1;

        return IntStream.range(start, start + count)
                .mapToObj(i -> prefix + (prefix.equals("W-") ? String.format("%03d", i) : i))
                .collect(Collectors.toList());
    }

}