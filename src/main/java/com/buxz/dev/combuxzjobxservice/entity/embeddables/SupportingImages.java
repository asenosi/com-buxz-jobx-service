package com.buxz.dev.combuxzjobxservice.entity.embeddables;

import com.buxz.dev.combuxzjobxservice.entity.BusinessProfileEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "supporting_images")
public class SupportingImages {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String uploadedDate;
    private String imageUrl;
    private String imageFileName;
    private String tag;

    @ManyToOne(fetch= FetchType.EAGER, optional=true, cascade= CascadeType.ALL)
    @JoinColumn(name = "supporting_images_id", nullable = true)
    private BusinessProfileEntity businessProfile;
}
