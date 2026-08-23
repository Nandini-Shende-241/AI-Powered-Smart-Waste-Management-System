package com.smartwaste.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartwaste.backend.Complaint;
import com.smartwaste.backend.User;
import com.smartwaste.backend.repository.ComplaintRepository;
import com.smartwaste.backend.repository.UserRepository;
import com.smartwaste.backend.entity.ComplaintStatusHistory;
import com.smartwaste.backend.repository.ComplaintStatusHistoryRepository;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class ComplaintController {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final ComplaintStatusHistoryRepository statusHistoryRepository;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Path uploadDirectory = Paths.get("uploads");

    public ComplaintController(
            ComplaintRepository complaintRepository,
            UserRepository userRepository,
            ComplaintStatusHistoryRepository statusHistoryRepository) {

        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    // =========================================================
    // CREATE COMPLAINT WITH IMAGE
    // =========================================================

    @PostMapping(
            value = "/with-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> createComplaintWithImage(

            @RequestParam("userId") Long userId,

            @RequestParam("description") String description,

            @RequestParam("location") String location,

            @RequestParam("image") MultipartFile image) {

        try {

            // -------------------------------------------------
            // Validate User ID
            // -------------------------------------------------

            if (userId == null) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error",
                                "User ID is required"
                        ));
            }

            // -------------------------------------------------
            // Check that user actually exists
            // -------------------------------------------------

            User user = userRepository.findById(userId)
                    .orElse(null);

            if (user == null) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error",
                                "User information not found. Please log in again."
                        ));
            }

            // -------------------------------------------------
            // Validate description
            // -------------------------------------------------

            if (description == null || description.isBlank()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error",
                                "Description is required"
                        ));
            }

            // -------------------------------------------------
            // Validate location
            // -------------------------------------------------

            if (location == null || location.isBlank()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error",
                                "Location is required"
                        ));
            }

            // -------------------------------------------------
            // Validate image
            // -------------------------------------------------

            if (image == null || image.isEmpty()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error",
                                "No image selected"
                        ));
            }

            // -------------------------------------------------
            // Create uploads folder
            // -------------------------------------------------

            Files.createDirectories(uploadDirectory);

            // -------------------------------------------------
            // Get original filename
            // -------------------------------------------------

            String originalFileName =
                    image.getOriginalFilename();

            if (originalFileName == null
                    || originalFileName.isBlank()) {

                originalFileName = "image.jpg";
            }

            // -------------------------------------------------
            // Get extension
            // -------------------------------------------------

            String extension = "";

            int dotIndex =
                    originalFileName.lastIndexOf(".");

            if (dotIndex >= 0) {

                extension =
                        originalFileName.substring(dotIndex);
            }

            // -------------------------------------------------
            // Generate unique filename
            // -------------------------------------------------

            String fileName =
                    UUID.randomUUID() + extension;

            Path filePath =
                    uploadDirectory.resolve(fileName);

            // -------------------------------------------------
            // Save image locally
            // -------------------------------------------------

            Files.copy(
                    image.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            System.out.println(
                    "Image saved at: " + filePath
            );

            // -------------------------------------------------
            // Prepare image for AI service
            // -------------------------------------------------

            byte[] imageBytes =
                    image.getBytes();

            final String finalFileName =
                    originalFileName;

            ByteArrayResource imageResource =
                    new ByteArrayResource(imageBytes) {

                        @Override
                        public String getFilename() {

                            return finalFileName;
                        }
                    };

            MultiValueMap<String, Object> body =
                    new LinkedMultiValueMap<>();

            body.add(
                    "image",
                    imageResource
            );

            // -------------------------------------------------
            // Default AI values
            // -------------------------------------------------

            String wasteType = "unknown";
            Double confidence = 0.0;

            // -------------------------------------------------
            // Send image to AI service
            // -------------------------------------------------

            try {

                System.out.println(
                        "Sending image to AI service..."
                );

                String aiResult =
                        restClient
                                .post()
                                .uri(
                                        "http://127.0.0.1:5000/predict"
                                )
                                .contentType(
                                        MediaType.MULTIPART_FORM_DATA
                                )
                                .body(body)
                                .retrieve()
                                .body(String.class);

                System.out.println(
                        "AI response: " + aiResult
                );

                // -------------------------------------------------
                // Parse AI response
                // -------------------------------------------------

                if (aiResult != null
                        && !aiResult.isBlank()) {

                    JsonNode json =
                            objectMapper.readTree(
                                    aiResult
                            );

                    if (json.has("wasteType")) {

                        wasteType =
                                json.get("wasteType")
                                        .asText();
                    }

                    if (json.has("confidence")) {

                        confidence =
                                json.get("confidence")
                                        .asDouble();
                    }
                }

            } catch (Exception aiException) {

                System.out.println(
                        "AI service unavailable: "
                                + aiException.getMessage()
                );

                wasteType = "unknown";
                confidence = 0.0;
            }

            System.out.println(
                    "Waste Type: " + wasteType
            );

            System.out.println(
                    "Confidence: " + confidence
            );

            // -------------------------------------------------
            // Create Complaint
            // -------------------------------------------------

            Complaint complaint =
                    new Complaint();

            complaint.setUserId(userId);

            complaint.setDescription(
                    description
            );

            complaint.setLocation(
                    location
            );

            complaint.setImagePath(
                    filePath.toString()
            );

            complaint.setWasteType(
                    wasteType
            );

            complaint.setConfidence(
                    confidence
            );

            complaint.setStatus(
                    "PENDING"
            );

            // -------------------------------------------------
// AUTOMATIC PRIORITY
// -------------------------------------------------

String automaticPriority = "MEDIUM";

if (wasteType != null) {

    String type = wasteType.toLowerCase();

    if (type.contains("e-waste")
            || type.contains("glass")) {

        automaticPriority = "HIGH";

    } else if (type.contains("paper")
            || type.contains("cardboard")) {

        automaticPriority = "LOW";

    } else {

        automaticPriority = "MEDIUM";
    }
}

complaint.setPriority(automaticPriority);

            complaint.setCreatedAt(
                    LocalDateTime.now()
            );

            // -------------------------------------------------
            // SAVE COMPLAINT TO MYSQL
            // -------------------------------------------------

            Complaint savedComplaint =
                    complaintRepository.save(
                            complaint
                    );

                    // -------------------------------------------------
// SAVE INITIAL STATUS HISTORY
// -------------------------------------------------

ComplaintStatusHistory history =
        new ComplaintStatusHistory();

history.setComplaintId(
        savedComplaint.getId()
);

history.setStatus(
        savedComplaint.getStatus()
);

statusHistoryRepository.save(history);

            System.out.println(
                    "Complaint saved successfully!"
            );

            System.out.println(
                    "Complaint ID: "
                            + savedComplaint.getId()
            );

            // =================================================
            // REWARD POINTS
            // =================================================

            Integer currentPoints =
                    user.getRewardPoints();

            if (currentPoints == null) {

                currentPoints = 0;
            }

            // Add 10 points for successful complaint

            Integer newPoints =
                    currentPoints + 10;

            user.setRewardPoints(
                    newPoints
            );

            userRepository.save(user);

            System.out.println(
                    "Reward points updated!"
            );

            System.out.println(
                    "User ID: " + user.getId()
            );

            System.out.println(
                    "Previous points: "
                            + currentPoints
            );

            System.out.println(
                    "New points: "
                            + newPoints
            );

            // -------------------------------------------------
            // Return response to frontend
            // -------------------------------------------------

            Map<String, Object> response =
                    new java.util.LinkedHashMap<>();

            response.put(
                    "message",
                    "Complaint submitted successfully"
            );

            response.put(
                    "complaintId",
                    savedComplaint.getId()
            );

            response.put(
                    "userId",
                    savedComplaint.getUserId()
            );

            response.put(
                    "wasteType",
                    savedComplaint.getWasteType()
            );

            response.put(
                    "confidence",
                    savedComplaint.getConfidence()
            );

            response.put(
                    "status",
                    savedComplaint.getStatus()
            );

            response.put(
                    "description",
                    savedComplaint.getDescription()
            );

            response.put(
                    "location",
                    savedComplaint.getLocation()
            );

            response.put(
                    "imagePath",
                    savedComplaint.getImagePath()
            );

            response.put(
                    "createdAt",
                    savedComplaint.getCreatedAt()
            );

            response.put(
                    "rewardPoints",
                    newPoints
            );

            return ResponseEntity.ok(response);

        } catch (IOException e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    "Failed to save image",

                                    "details",
                                    e.getMessage()
                            )
                    );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    "Failed to create complaint",

                                    "details",
                                    e.getMessage()
                            )
                    );
        }
    }

    // =========================================================
    // GET ALL COMPLAINTS
    // =========================================================

    @GetMapping
    public ResponseEntity<?> getAllComplaints() {

        return ResponseEntity.ok(
                complaintRepository.findAll()
        );
    }

    // =========================================================
    // GET COMPLAINT BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaintById(
            @PathVariable Long id) {

        return complaintRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    // =========================================================
// GET COMPLAINT STATUS HISTORY
// =========================================================

@GetMapping("/{id}/status-history")
public ResponseEntity<?> getComplaintStatusHistory(
        @PathVariable Long id) {

    // Check whether complaint exists
    if (!complaintRepository.existsById(id)) {

        return ResponseEntity.notFound().build();
    }

    // Get status history in chronological order
    List<ComplaintStatusHistory> history =
            statusHistoryRepository
                    .findByComplaintIdOrderByChangedAtAsc(id);

    return ResponseEntity.ok(history);
}

    // =========================================================
    // GET COMPLAINTS BY USER
    // =========================================================

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getComplaintsByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(

                complaintRepository.findAll()
                        .stream()
                        .filter(complaint ->
                                complaint.getUserId()
                                        .equals(userId))
                        .toList()
        );
    }
}