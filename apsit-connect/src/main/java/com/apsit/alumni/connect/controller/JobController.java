package com.apsit.alumni.connect.controller;

import com.apsit.alumni.connect.dto.CreateJobRequest;
import com.apsit.alumni.connect.dto.JobDto;
import com.apsit.alumni.connect.model.User;
import com.apsit.alumni.connect.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // --- NEW IMPORT ---

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // --- Endpoint 1: Get All Jobs (for everyone) ---
    @GetMapping
    public ResponseEntity<List<JobDto>> getAllJobs() {
        List<JobDto> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    // --- Endpoint 2: Post a New Job (Alumni Only) ---
    @PostMapping
    @PreAuthorize("hasRole('ALUMNI')")
    public ResponseEntity<JobDto> postJob(
            @RequestBody CreateJobRequest request,
            @AuthenticationPrincipal User userDetails
    ) {
        JobDto newJob = jobService.postJob(request, userDetails.getEmail());
        return new ResponseEntity<>(newJob, HttpStatus.CREATED);
    }

    // --- NEW ENDPOINT 3: Admin Delete Job ---
    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasRole('ADMIN')") // LOCKED for Admins only
    public ResponseEntity<Map<String, String>> deleteJob(
            @PathVariable Long jobId
    ) {
        jobService.deleteJob(jobId);
        // Return a simple success message
        return ResponseEntity.ok(Map.of("message", "Job deleted successfully."));
    }
    // --- END NEW ENDPOINT ---
}