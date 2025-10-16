package com.buxz.dev.combuxzjobxservice.controller;

import com.buxz.dev.combuxzjobxservice.domain.JobDto;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobCurrentState;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JobControllerEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPublishAndRetrieveJobFlow() throws Exception {
        JobDto jobDto = new JobDto();
        jobDto.setJobTitle("Integration Engineer");
        jobDto.setJobDescription("Own the integration test strategy");
        jobDto.setEmployer("JobX");
        jobDto.setJobType(JobType.FULLTIME);
        jobDto.setJobState(JobCurrentState.CREATED);
        jobDto.setSalary("R600k");
        jobDto.setJobCity("Cape Town");
        jobDto.setClosingDate(LocalDate.now().plusDays(7));

        MvcResult createResult = mockMvc.perform(post("/v1/jobx/jobs/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.jobTitle").value("Integration Engineer"))
                .andReturn();

        JsonNode createPayload = objectMapper.readTree(createResult.getResponse().getContentAsString());
        int jobId = createPayload.get("id").asInt();

        mockMvc.perform(put("/v1/jobx/jobs/" + jobId + "/publish"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("JobEntry Successfully Published"));

        MvcResult getResult = mockMvc.perform(get("/v1/jobx/jobs/" + jobId))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jobPayload = objectMapper.readTree(getResult.getResponse().getContentAsString());
        assertThat(jobPayload.get("jobTitle").asText()).isEqualTo("Integration Engineer");
        assertThat(jobPayload.get("jobState").asText()).isEqualTo(JobCurrentState.PUBLISHED.name());
        assertThat(jobPayload.get("published").asBoolean()).isTrue();
    }
}
