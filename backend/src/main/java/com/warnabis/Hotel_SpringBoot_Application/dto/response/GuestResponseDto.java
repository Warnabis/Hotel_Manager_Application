package com.warnabis.Hotel_SpringBoot_Application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestResponseDto {

    private Integer id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String status;
    private List<Integer> serviceIds;
}
