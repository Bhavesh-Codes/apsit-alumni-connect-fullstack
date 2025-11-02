package com.apsit.alumni.connect.service;

import com.apsit.alumni.connect.dto.CreateJobRequest;
import com.apsit.alumni.connect.dto.JobDto;
import com.apsit.alumni.connect.model.Job;
import com.apsit.alumni.connect.model.User;
import com.apsit.alumni.connect.repository.JobRepository;
import com.apsit.alumni.connect.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    // --- THIS IS THE FIX ---
    public JobDto mapToJobDto(Job job) {
    // --- END OF FIX ---
        JobDto dto = new JobDto();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setCompany(job.getCompany());
        dto.setLocation(job.getLocation());
        dto.setType(job.getType());
        dto.setDescription(job.getDescription());
        dto.setRequirements(job.getRequirements());
        dto.setSalary(job.getSalary());
        dto.setApplicationDeadline(job.getApplicationDeadline());
        dto.setCreatedAt(job.getCreatedAt().toString());
        
        User poster = job.getPostedBy();
        dto.setPostedById(poster.getId());
        dto.setPostedByName(poster.getName());
        dto.setPostedByCompany(poster.getCompany()); 

        return dto;
    }

    public List<JobDto> getAllJobs() {
        return jobRepository.findAll()
                .stream()
                .map(this::mapToJobDto)
                .collect(Collectors.toList());
    }

    public JobDto postJob(CreateJobRequest request, String userEmail) {
        User poster = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setType(request.getType());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setSalary(request.getSalary());
        job.setApplicationDeadline(request.getApplicationDeadline());
        job.setPostedBy(poster); 

        Job savedJob = jobRepository.save(job);
        return mapToJobDto(savedJob);
    }

    public void deleteJob(Long jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new IllegalArgumentException("Job not found with ID: " + jobId);
        }
        jobRepository.deleteById(jobId);
    }
}