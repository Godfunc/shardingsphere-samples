package com.godfunc.shardingsphere.dto;

import lombok.Data;

@Data
public class UserOrderDTO {

    private Long userId;

    private String name;

    private Long totalAmount;

    private Integer count;
}
