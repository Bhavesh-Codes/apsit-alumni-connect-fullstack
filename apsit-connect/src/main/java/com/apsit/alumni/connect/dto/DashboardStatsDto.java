package com.apsit.alumni.connect.dto;

import lombok.Data;

@Data
public class DashboardStatsDto {
    private long totalUsers;
    private long totalStudents;
    private long totalAlumni;
    private long pendingVerifications;
    private long totalEvents;
    private long totalJobs;
}