package com.warnabis.Hotel_SpringBoot_Application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponseDto {

    private Integer id;
    private String title;
    private String description;
    private Double price;
    private String duration;
    private List<Integer> guestIds;
    private List<Integer> employeeIds;
}
