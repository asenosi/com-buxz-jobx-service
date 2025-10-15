package com.buxz.dev.combuxzjobxservice.entity.embeddables;

import lombok.Data;

import jakarta.persistence.Embeddable;

@Data
@Embeddable
public class BusinessAddress {
    private String streetNo;
    private String streetName;
    private String suburb;
    private String city;
}
