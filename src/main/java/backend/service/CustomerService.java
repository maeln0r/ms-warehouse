package backend.service;

import backend.dto.CustomerDto;

import java.util.List;

public interface CustomerService {

    List<CustomerDto> get();
}
