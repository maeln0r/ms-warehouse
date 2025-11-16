package backend.kafka;

import backend.dto.CargoDto;
import backend.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CargoConsumer {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(
            topics = "${kafka.topic}",
            groupId = "cargo-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(String json) throws JsonProcessingException {
        List<CargoDto> cargos = objectMapper.readValue(
                json,
                new TypeReference<>() {}
        );

        for (CargoDto cargo : cargos) {
            log.info("🔹 {}", cargo.getName());
        }

        log.info("✅ Received {} cargos", cargos.size());

        orderService.saveFromCargos(cargos);

    }
}


