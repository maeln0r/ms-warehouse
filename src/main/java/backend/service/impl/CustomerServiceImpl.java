package backend.service.impl;

import backend.dto.CustomerDto;
import backend.feign.CustomerClient;
import backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerClient customerClient;

    @Retryable(
            include = {
                    feign.FeignException.class,          // ошибки HTTP от удалёнки
                    feign.RetryableException.class,      // сетевые/таймауты Feign
                    org.springframework.web.client.ResourceAccessException.class,
                    java.net.SocketTimeoutException.class
            },
            maxAttempts = 5,                         // 1 (основная) + 4 повтора
            backoff = @Backoff(delay = 2_000, multiplier = 2.0, maxDelay = 30_000)
    )
    @Override
    public List<CustomerDto> get() {
        return customerClient.getAll();
    }
}
