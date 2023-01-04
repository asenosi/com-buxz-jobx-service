package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.domain.TestimonialDto;
import com.buxz.dev.combuxzjobxservice.entity.TestimonialEntity;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.repository.TestimonialRepository;
import com.buxz.dev.combuxzjobxservice.repository.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TestimonialService {

    private final TestimonialRepository testimonialRepository;
    private final UserProfileRepository userProfileRepository;
    private final UploadService uploadService;

    @Autowired
    public TestimonialService(TestimonialRepository testimonialRepository, UserProfileRepository userProfileRepository, UploadService uploadService) {
        this.testimonialRepository = testimonialRepository;
        this.userProfileRepository = userProfileRepository;
        this.uploadService = uploadService;
    }

    public TestimonialEntity addTestimonial(int id, TestimonialDto testimonialDto) {
        Optional<UserProfileEntity> userProfile = userProfileRepository.findById(id);
        TestimonialEntity testimonial = new TestimonialEntity();
        if (userProfile.isPresent()) {
            List<TestimonialEntity> testimonialList = userProfile.get().getTestimonialEntity();
            testimonial.setTestimonialBy(testimonialDto.getTestimonialBy());
            testimonial.setTestimonialSummary(testimonialDto.getTestimonialSummary());
            testimonial.setTestimonialDescription(testimonialDto.getTestimonialDescription());
            testimonialList.add(testimonial);
            testimonialRepository.saveAndFlush(testimonial);
            userProfile.get().setTestimonialEntity(testimonialList);
            userProfileRepository.save(userProfile.get());
            log.info("AddTestimonial : Added testimonial to profile_id {}", id);
        } else {
            log.warn("AddTestimonial : Failed to add testimonial, because profile_id {} not found", id);
        }
        return testimonial;
    }

    public TestimonialEntity updateTestimonial(int userId, int testimonyId, TestimonialDto testimonialDto) {
        Optional<TestimonialEntity> testimonial = testimonialRepository.findById(testimonyId);
        if (testimonial.isPresent()) {
            testimonial.get().setTestimonialBy(testimonialDto.getTestimonialBy());
            testimonial.get().setTestimonialSummary(testimonialDto.getTestimonialSummary());
            testimonial.get().setTestimonialDescription(testimonialDto.getTestimonialDescription());
            testimonialRepository.save(testimonial.get());
            log.info("UpdateTestimonial : Updated testimonial for profile_id {}", userId);
        } else {
            log.warn("UpdateTestimonial : Failed to update testimonial to profile_id {}, no testimonial with id {} found", userId, testimonyId);
        }
        return testimonial.get();
    }

    public ResponseMessage deleteTestimonial(int userId, int testimonyId) {
        testimonialRepository.deleteById(testimonyId);
        return new ResponseMessage("DeleteTestimonial : Testimonial deleted successfully");
    }

    public Optional<TestimonialEntity> getTestimonialById(int testimonialId) {
        return testimonialRepository.findById(testimonialId);
    }

    public ResponseMessage uploadTestimonial(int userId, MultipartFile file) {
        Optional<UserProfileEntity> userProfile = userProfileRepository.findById(userId);
        TestimonialEntity testimonial = new TestimonialEntity();
        if (userProfile.isPresent()) {
            List<TestimonialEntity> testimonialList = userProfile.get().getTestimonialEntity();
            testimonial.setFileName(file.getOriginalFilename());
            uploadService.uploadFile(file);
            testimonialList.add(testimonial);
            testimonialRepository.saveAndFlush(testimonial);
            userProfile.get().setTestimonialEntity(testimonialList);
            userProfileRepository.save(userProfile.get());
            log.info("UploadAddTestimonial : Added testimonial to profile_id {}", userId);
            return new ResponseMessage("Uploaded the file successfully: " + file.getOriginalFilename());

        } else {
            log.warn("UploadAddTestimonial : Failed to add&upload testimonial, because profile_id {} not found", userId);
            return new ResponseMessage("Failed to add&upload testimonial, because profile not found");
        }
    }
}
