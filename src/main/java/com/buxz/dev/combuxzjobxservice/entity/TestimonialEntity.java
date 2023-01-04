package com.buxz.dev.combuxzjobxservice.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "testimonial")
public class TestimonialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String testimonialBy;
    private String testimonialSummary;
    @Lob
    private String testimonialDescription;
    private String fileName;
}
