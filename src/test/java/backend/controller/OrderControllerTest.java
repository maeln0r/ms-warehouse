//package backend.controller;
//
//import backend.WarehouseApplication;
//import backend.dto.OrderDto;
//import backend.service.OrderService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.test.context.support.WithMockUser;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest(classes = WarehouseApplication.class)
//@AutoConfigureMockMvc
//@WithMockUser // авторизованный пользователь
//public class OrderControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private OrderService orderService;
//
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setup() {
//        objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule()); // для Instant
//    }
//
//    @Test
//    void getById_shouldReturnOrderDto() throws Exception {
//        OrderDto mockOrder = OrderDto.builder()
//                .id(1L)
//                .orderName("Test Order")
//                .price(new BigDecimal("123.45"))
//                .createdDate(Instant.now())
//                .build();
//
//        Mockito.when(orderService.getById(1L)).thenReturn(mockOrder);
//
//        mockMvc.perform(get("/api/orders/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.orderName").value("Test Order"));
//    }
//
//    @Test
//    void getAll_shouldReturnListOfOrders() throws Exception {
//        List<OrderDto> mockOrders = List.of(
//                OrderDto.builder().id(1L).orderName("Order 1").build(),
//                OrderDto.builder().id(2L).orderName("Order 2").build()
//        );
//
//        Mockito.when(orderService.getAll()).thenReturn(mockOrders);
//
//        mockMvc.perform(get("/api/orders/all"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(2));
//    }
//
//    @Test
//    void deleteById_shouldReturnNoContent() throws Exception {
//        Mockito.doNothing().when(orderService).deleteById(anyLong());
//
//        mockMvc.perform(delete("/api/orders/1"))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void isDeleted_shouldWork() throws Exception {
//        OrderDto mockOrder = OrderDto.builder()
//                .id(1L)
//                .orderName("Test Order")
//                .price(new BigDecimal("123.45"))
//                .createdDate(Instant.now())
//                .build();
//
//        Mockito.when(orderService.isDeleted(1L, true)).thenReturn(mockOrder);
//
//        mockMvc.perform(patch("/api/orders/1/isDeleted").param("isDeleted", "true"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void changeStatus_shouldWork() throws Exception {
//        OrderDto mockOrder = OrderDto.builder()
//                .id(1L)
//                .orderName("Test Order")
//                .price(new BigDecimal("123.45"))
//                .createdDate(Instant.now())
//                .build();
//
//        Mockito.when(orderService.changeStatus(1L, "PENDING")).thenReturn(mockOrder);
//
//        mockMvc.perform(patch("/api/orders/1/PENDING"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void updateOrderDetails_shouldWork() throws Exception {
//        OrderDto mockOrder = OrderDto.builder()
//                .id(1L)
//                .orderName("Test Order")
//                .price(new BigDecimal("123.45"))
//                .createdDate(Instant.now())
//                .build();
//
//        Mockito.when(orderService.updateOrderDetails(1L, true, 5L, new BigDecimal("999.99"))).thenReturn(mockOrder);
//
//        mockMvc.perform(patch("/api/orders/1/update")
//                        .param("notProcess", "true")
//                        .param("warehouseId", "5")
//                        .param("price", "999.99"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void saveOrder_shouldWork() throws Exception {
//        OrderDto mockOrder = OrderDto.builder()
//                .id(1L)
//                .orderName("Test Order")
//                .price(new BigDecimal("123.45"))
//                .createdDate(Instant.now())
//                .build();
//
//        Mockito.when(orderService.saveOrder(Mockito.any())).thenReturn(mockOrder);
//
//        mockMvc.perform(post("/api/orders/save")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(mockOrder)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void getOrderIdsByWarehouse_shouldWork() throws Exception {
//        Mockito.when(orderService.getOrderIdsByWarehouse(1L)).thenReturn(List.of(1L, 2L));
//
//        mockMvc.perform(get("/api/orders/warehouse/1/orders"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void transferOrderToAnotherWarehouse_shouldWork() throws Exception {
//        OrderDto mockOrder = OrderDto.builder()
//                .id(1L)
//                .orderName("Test Order")
//                .price(new BigDecimal("123.45"))
//                .createdDate(Instant.now())
//                .build();
//
//        Mockito.when(orderService.transferOrderToAnotherWarehouse(1L, 2L)).thenReturn(mockOrder);
//
//        mockMvc.perform(patch("/api/orders/1/transfer/2"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void transferOrdersToAnotherWarehouse_shouldWork() throws Exception {
//        OrderDto mockOrder = OrderDto.builder()
//                .id(1L)
//                .orderName("Test Order")
//                .price(new BigDecimal("123.45"))
//                .createdDate(Instant.now())
//                .build();
//
//        Mockito.when(orderService.transferOrdersToAnotherWarehouse(List.of(1L, 2L), 3L)).thenReturn(List.of(mockOrder));
//
//        mockMvc.perform(patch("/api/orders/transfer")
//                        .param("orderIds", "1", "2")
//                        .param("newWarehouseId", "3"))
//                .andExpect(status().isOk());
//    }
//
//}