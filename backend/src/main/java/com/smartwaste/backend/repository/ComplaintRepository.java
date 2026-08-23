package com.smartwaste.backend.repository;

import com.smartwaste.backend.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT c.wasteType, COUNT(c) FROM Complaint c " +
           "WHERE c.wasteType IS NOT NULL " +
           "GROUP BY c.wasteType")
    List<Object[]> countComplaintsByWasteType();
}