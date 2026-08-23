package com.smartwaste.backend;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "cleanup_team_id")
    private Long cleanupTeamId;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(name = "image_path")
    private String imagePath;

    @Column(length = 30)
    private String status = "PENDING";

    @Column(length = 20)
    private String priority = "MEDIUM";

    @Column(name = "waste_type", length = 50)
    private String wasteType;

    @Column
    private Double confidence;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Complaint() {
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
    return priority;
}

public void setPriority(String priority) {
    this.priority = priority;
}

    public String getWasteType() {
        return wasteType;
    }

    public void setWasteType(String wasteType) {
        this.wasteType = wasteType;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCleanupTeamId() {
    return cleanupTeamId;
}

public void setCleanupTeamId(Long cleanupTeamId) {
    this.cleanupTeamId = cleanupTeamId;
}
}