package com.buxz.dev.combuxzjobxservice.entity.embeddables;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class ContactDetails {
    private String email;
    private String whatsappNumber;
    private String linkedInLink;
    private String facebookLink;
}
