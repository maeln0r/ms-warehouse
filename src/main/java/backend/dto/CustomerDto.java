package backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String city;
}
