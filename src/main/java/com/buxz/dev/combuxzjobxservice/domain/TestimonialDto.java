package com.buxz.dev.combuxzjobxservice.domain;

import lombok.Data;

import javax.persistence.Lob;

@Data
public class TestimonialDto {
    private String testimonialBy;
    private String testimonialSummary;
    @Lob
    private String testimonialDescription;
    private String fileName;
}
