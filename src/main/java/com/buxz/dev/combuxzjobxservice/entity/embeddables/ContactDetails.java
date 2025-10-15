package com.buxz.dev.combuxzjobxservice.entity.embeddables;

import lombok.Builder;
import lombok.Data;

import jakarta.persistence.Embeddable;

@Data
@Builder
@Embeddable
public class ContactDetails {
    private String email;
    private String whatsappNumber;
    private String linkedInLink;
    private String facebookLink;
}
