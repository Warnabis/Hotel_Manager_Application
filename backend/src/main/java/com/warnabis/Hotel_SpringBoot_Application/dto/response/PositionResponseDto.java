package com.warnabis.Hotel_SpringBoot_Application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionResponseDto {

    private Integer id;
    private String title;
    private Double salary;
    private String responsibilities;
    private List<Integer> employeeIds;
}
