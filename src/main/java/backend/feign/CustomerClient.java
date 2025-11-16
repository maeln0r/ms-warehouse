package backend.feign;

import backend.dto.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "customerClient", url = "${customer.url}")
public interface CustomerClient {

    @GetMapping("/api/customer/all/list")
    List<CustomerDto> getAll();
}
