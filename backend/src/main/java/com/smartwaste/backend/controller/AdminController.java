package com.smartwaste.backend.controller;

import com.smartwaste.backend.Complaint;
import com.smartwaste.backend.Feedback;
import com.smartwaste.backend.entity.CleanupTeam;
import com.smartwaste.backend.repository.CleanupTeamRepository;
import com.smartwaste.backend.repository.ComplaintRepository;
import com.smartwaste.backend.repository.FeedbackRepository;
import com.smartwaste.backend.entity.ComplaintStatusHistory;
import com.smartwaste.backend.repository.ComplaintStatusHistoryRepository;
import com.smartwaste.backend.entity.Notification;
import com.smartwaste.backend.repository.NotificationRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final ComplaintRepository complaintRepository;
    private final FeedbackRepository feedbackRepository;
    private final CleanupTeamRepository cleanupTeamRepository;
    private final ComplaintStatusHistoryRepository statusHistoryRepository;
    private final NotificationRepository notificationRepository;

    public AdminController(
            ComplaintRepository complaintRepository,
            FeedbackRepository feedbackRepository,
            CleanupTeamRepository cleanupTeamRepository,
            ComplaintStatusHistoryRepository statusHistoryRepository,
        NotificationRepository notificationRepository){

        this.complaintRepository = complaintRepository;
        this.feedbackRepository = feedbackRepository;
        this.cleanupTeamRepository = cleanupTeamRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.notificationRepository = notificationRepository;
    }


    // =====================================================
    // GET ALL COMPLAINTS
    // =====================================================

    @GetMapping("/complaints")
    public ResponseEntity<?> getAllComplaints() {

        try {

            List<Complaint> complaints =
                    complaintRepository.findAll();
        
           
                    
            // Automatic priority escalation
            for (Complaint complaint : complaints) {
                updatePriorityBasedOnWaitingTime(complaint);
            }

            return ResponseEntity.ok(complaints);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    "Failed to load complaints"
                            )
                    );
        }
    }


    // =====================================================
    // GET COMPLAINT BY ID
    // =====================================================

    @GetMapping("/complaints/{id}")
    public ResponseEntity<?> getComplaintById(
            @PathVariable Long id) {

        return complaintRepository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // =====================================================
    // UPDATE COMPLAINT STATUS
    // =====================================================

    @PutMapping("/complaints/{id}/status")
    public ResponseEntity<?> updateComplaintStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        try {

            String newStatus =
                    request.get("status");

            // Validate status
            if (newStatus == null ||
                    newStatus.isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "Status is required"
                                )
                        );
            }

            newStatus =
                    newStatus
                            .trim()
                            .toUpperCase();

            // Allow only valid statuses
            if (!newStatus.equals("PENDING") &&
                    !newStatus.equals("IN PROGRESS") &&
                    !newStatus.equals("COMPLETED") &&
                    !newStatus.equals("REJECTED")) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "Invalid status"
                                )
                        );
            }

            // Find complaint
            Complaint complaint =
                    complaintRepository
                            .findById(id)
                            .orElse(null);

            if (complaint == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            // =================================================
            // RELEASE CLEANUP TEAM WHEN COMPLETED
            // =================================================

            if ("COMPLETED".equalsIgnoreCase(newStatus)) {

                Long cleanupTeamId =
                        complaint.getCleanupTeamId();

                if (cleanupTeamId != null) {

                    CleanupTeam team =
                            cleanupTeamRepository
                                    .findById(cleanupTeamId)
                                    .orElse(null);

                    if (team != null) {

                        team.setStatus("AVAILABLE");

                        cleanupTeamRepository.save(team);

                        System.out.println(
                                "Cleanup team released successfully!"
                        );

                        System.out.println(
                                "Team ID: " + team.getId()
                        );
                    }
                }
            }


            // =================================================
            // UPDATE STATUS
            // =================================================

            complaint.setStatus(newStatus);

            Complaint updatedComplaint =
                    complaintRepository.save(complaint);


            // =================================================
            // SAVE STATUS HISTORY
            // =================================================

            ComplaintStatusHistory history =
                    new ComplaintStatusHistory();

            history.setComplaintId(
                    updatedComplaint.getId()
            );

            history.setStatus(
                    updatedComplaint.getStatus()
            );

            statusHistoryRepository.save(history);

            // =================================================
// CREATE USER NOTIFICATION
// =================================================

Notification notification =
        new Notification(
                updatedComplaint.getUserId(),
                "Your complaint #" +
                        updatedComplaint.getId() +
                        " status has been updated to " +
                        updatedComplaint.getStatus()
        );

notificationRepository.save(notification);

System.out.println(
        "Notification created for User ID: " +
                updatedComplaint.getUserId()
); 

            System.out.println(
                    "Complaint status updated!"
            );

            System.out.println(
                    "Complaint ID: " +
                            updatedComplaint.getId()
            );

            System.out.println(
                    "New Status: " +
                            updatedComplaint.getStatus()
            );


            return ResponseEntity.ok(
                    updatedComplaint
            );


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    "Failed to update complaint status",

                                    "details",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // GET ALL FEEDBACK
    // =====================================================

    @GetMapping("/feedback")
    public ResponseEntity<?> getAllFeedback() {

        try {

            List<Feedback> feedbackList =
                    feedbackRepository.findAll();

            return ResponseEntity.ok(
                    feedbackList
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    "Failed to load feedback"
                            )
                    );
        }
    }


    // =====================================================
    // DASHBOARD STATISTICS
    // =====================================================

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {

        try {

            List<Complaint> complaints =
                    complaintRepository.findAll();

            long total =
                    complaints.size();

            long pending =
                    complaints.stream()
                            .filter(c ->
                                    "PENDING"
                                            .equalsIgnoreCase(
                                                    c.getStatus()
                                            )
                            )
                            .count();

            long inProgress =
                    complaints.stream()
                            .filter(c ->
                                    "IN PROGRESS"
                                            .equalsIgnoreCase(
                                                    c.getStatus()
                                            )
                            )
                            .count();

            long completed =
                    complaints.stream()
                            .filter(c ->
                                    "COMPLETED"
                                            .equalsIgnoreCase(
                                                    c.getStatus()
                                            )
                            )
                            .count();

            long rejected =
                    complaints.stream()
                            .filter(c ->
                                    "REJECTED"
                                            .equalsIgnoreCase(
                                                    c.getStatus()
                                            )
                            )
                            .count();

            return ResponseEntity.ok(
                    Map.of(
                            "totalComplaints",
                            total,

                            "pendingComplaints",
                            pending,

                            "inProgressComplaints",
                            inProgress,

                            "completedComplaints",
                            completed,

                            "rejectedComplaints",
                            rejected
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    "Failed to load statistics"
                            )
                    );
        }
    }

    // =====================================================
// WASTE CATEGORY STATISTICS
// =====================================================

@GetMapping("/waste-statistics")
public ResponseEntity<?> getWasteCategoryStatistics() {

    try {

        List<Object[]> results =
                complaintRepository.countComplaintsByWasteType();

        Map<String, Long> statistics =
                new java.util.LinkedHashMap<>();

        for (Object[] row : results) {

            String wasteType = (String) row[0];
            Long count = ((Number) row[1]).longValue();

            statistics.put(wasteType, count);
        }

        return ResponseEntity.ok(statistics);

    } catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity
                .internalServerError()
                .body(
                        Map.of(
                                "error",
                                "Failed to load waste category statistics"
                        )
                );
    }
}

// =====================================================
// AI WASTE INSIGHTS & RECOMMENDATIONS
// =====================================================

@GetMapping("/waste-insights")
public ResponseEntity<?> getWasteInsights() {

    try {

        List<Complaint> complaints =
                complaintRepository.findAll();

        if (complaints.isEmpty()) {

            return ResponseEntity.ok(
                    Map.of(
                            "insight",
                            "No waste complaints available yet.",
                            "recommendation",
                            "Continue collecting waste reports to generate AI-based insights."
                    )
            );
        }

        // Count waste types
        Map<String, Long> wasteTypeCounts =
                complaints.stream()
                        .filter(c -> c.getWasteType() != null)
                        .collect(
                                java.util.stream.Collectors.groupingBy(
                                        Complaint::getWasteType,
                                        java.util.stream.Collectors.counting()
                                )
                        );

        String mostCommonWasteType =
                wasteTypeCounts.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("Unknown");

        long mostCommonWasteCount =
                wasteTypeCounts.getOrDefault(
                        mostCommonWasteType,
                        0L
                );

        // Count pending complaints
        long pendingCount =
                complaints.stream()
                        .filter(c ->
                                "PENDING".equalsIgnoreCase(
                                        c.getStatus()
                                )
                        )
                        .count();

        // Count high priority complaints
        long highPriorityCount =
                complaints.stream()
                        .filter(c ->
                                "HIGH".equalsIgnoreCase(
                                        c.getPriority()
                                )
                        )
                        .count();

        // Average AI confidence
        double averageConfidence =
                complaints.stream()
                        .filter(c -> c.getConfidence() != null)
                        .mapToDouble(Complaint::getConfidence)
                        .average()
                        .orElse(0.0);

        String insight =
                "The most commonly reported waste type is "
                + mostCommonWasteType
                + " with "
                + mostCommonWasteCount
                + " complaint(s). "
                + pendingCount
                + " complaint(s) are currently pending.";

        String recommendation;

        if (highPriorityCount > 0) {

            recommendation =
                    "There are "
                    + highPriorityCount
                    + " high-priority complaint(s). "
                    + "Consider prioritizing cleanup teams for these complaints.";

        } else if (pendingCount > 0) {

            recommendation =
                    "There are "
                    + pendingCount
                    + " pending complaint(s). "
                    + "Consider assigning available cleanup teams to reduce waiting time.";

        } else {

            recommendation =
                    "All reported complaints are currently handled. "
                    + "Continue monitoring waste trends and collection requirements.";
        }

        return ResponseEntity.ok(
                Map.of(
                        "mostCommonWasteType",
                        mostCommonWasteType,

                        "mostCommonWasteCount",
                        mostCommonWasteCount,

                        "pendingComplaints",
                        pendingCount,

                        "highPriorityComplaints",
                        highPriorityCount,

                        "averageConfidence",
                        Math.round(averageConfidence * 100.0) / 100.0,

                        "insight",
                        insight,

                        "recommendation",
                        recommendation
                )
        );

    } catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity
                .internalServerError()
                .body(
                        Map.of(
                                "error",
                                "Failed to generate waste insights",
                                "details",
                                e.getMessage()
                        )
                );
    }
}


    // =====================================================
    // ASSIGN CLEANUP TEAM
    // =====================================================

    @PutMapping(
            "/complaints/{complaintId}/assign-team/{teamId}"
    )
    public ResponseEntity<?> assignCleanupTeam(
            @PathVariable Long complaintId,
            @PathVariable Long teamId) {

        try {

            // Find complaint
            Complaint complaint =
                    complaintRepository
                            .findById(complaintId)
                            .orElse(null);

            if (complaint == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            // Find cleanup team
            CleanupTeam team =
                    cleanupTeamRepository
                            .findById(teamId)
                            .orElse(null);

            if (team == null) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "Cleanup team not found"
                                )
                        );
            }


            // Check team availability
            if (!"AVAILABLE".equalsIgnoreCase(
                    team.getStatus())) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "Cleanup team is not available"
                                )
                        );
            }


            // Assign team
            complaint.setCleanupTeamId(
                    team.getId()
            );


            // Complaint becomes IN PROGRESS
            complaint.setStatus(
                    "IN PROGRESS"
            );


            // Team becomes BUSY
            team.setStatus(
                    "BUSY"
            );


            // Save both
            complaintRepository.save(
                    complaint
            );

            cleanupTeamRepository.save(
                    team
            );


            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Cleanup team assigned successfully",

                            "complaintId",
                            complaint.getId(),

                            "cleanupTeamId",
                            team.getId(),

                            "status",
                            complaint.getStatus(),

                            "teamStatus",
                            team.getStatus()
                    )
            );


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    "Failed to assign cleanup team",

                                    "details",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // GET COMPLAINT STATUS HISTORY
    // =====================================================

    @GetMapping("/complaints/{id}/history")
    public ResponseEntity<?> getComplaintStatusHistory(
            @PathVariable Long id) {

        try {

            // Check complaint exists
            if (!complaintRepository.existsById(id)) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            List<ComplaintStatusHistory> history =
                    statusHistoryRepository
                            .findByComplaintIdOrderByChangedAtAsc(id);

            return ResponseEntity.ok(history);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    "Failed to load complaint status history"
                            )
                    );
        }
    }


    // =====================================================
    // UPDATE COMPLAINT PRIORITY
    // =====================================================

    @PutMapping("/complaints/{id}/priority")
    public ResponseEntity<?> updateComplaintPriority(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        try {

            String newPriority =
                    request.get("priority");

            if (newPriority == null ||
                    newPriority.isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "Priority is required"
                                )
                        );
            }

            newPriority =
                    newPriority
                            .trim()
                            .toUpperCase();


            // Allow only valid priorities
            if (!newPriority.equals("LOW") &&
                    !newPriority.equals("MEDIUM") &&
                    !newPriority.equals("HIGH")) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "Invalid priority"
                                )
                        );
            }


            // Find complaint
            Complaint complaint =
                    complaintRepository
                            .findById(id)
                            .orElse(null);

            if (complaint == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            // Update priority
            complaint.setPriority(
                    newPriority
            );

            Complaint updatedComplaint =
                    complaintRepository.save(
                            complaint
                    );

            return ResponseEntity.ok(
                    updatedComplaint
            );


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    "Failed to update complaint priority",

                                    "details",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // AUTOMATIC PRIORITY ESCALATION
    // =====================================================

    private void updatePriorityBasedOnWaitingTime(
            Complaint complaint) {

        if (complaint == null) {
            return;
        }


        // Only pending complaints
        if (!"PENDING".equalsIgnoreCase(
                complaint.getStatus())) {

            return;
        }


        // Created time must exist
        if (complaint.getCreatedAt() == null) {
            return;
        }


        long waitingHours =
                Duration.between(
                        complaint.getCreatedAt(),
                        LocalDateTime.now()
                ).toHours();


        // Pending for 48 hours or more
        // → HIGH priority

        if (waitingHours >= 48) {

            if (!"HIGH".equalsIgnoreCase(
                    complaint.getPriority())) {

                complaint.setPriority(
                        "HIGH"
                );

                complaintRepository.save(
                        complaint
                );
            }
        }
    }
}