package com.buxz.dev.combuxzjobxservice.entity.embeddables;

import lombok.Builder;
import lombok.Data;

import jakarta.persistence.Embeddable;

@Data
@Builder
@Embeddable
public class BusinessAddress {
    private String streetNo;
    private String streetName;
    private String suburb;
    private String city;
}
