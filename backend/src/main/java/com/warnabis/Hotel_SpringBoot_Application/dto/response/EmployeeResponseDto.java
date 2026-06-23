package com.warnabis.Hotel_SpringBoot_Application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDto {

    private Integer id;
    private String fullName;
    private String experience;
    private String schedule;
    private String phoneNumber;
    private List<Integer> positionIds;
    private List<Integer> roomIds;
    private List<Integer> serviceIds;
}
