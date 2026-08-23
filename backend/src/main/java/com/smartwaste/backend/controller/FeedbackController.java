package com.smartwaste.backend.controller;

import com.smartwaste.backend.Feedback;
import com.smartwaste.backend.User;
import com.smartwaste.backend.repository.FeedbackRepository;
import com.smartwaste.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public FeedbackController(
            FeedbackRepository feedbackRepository,
            UserRepository userRepository) {

        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }


    // ==========================================
    // SUBMIT FEEDBACK
    // ==========================================

    @PostMapping
    public ResponseEntity<?> submitFeedback(
            @RequestBody Feedback feedback) {

        try {

            // ------------------------------------------
            // Validate User ID
            // ------------------------------------------

            if (feedback.getUserId() == null) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error",
                                "User ID is required"
                        ));
            }


            // ------------------------------------------
            // Check User
            // ------------------------------------------

            User user =
                    userRepository.findById(
                            feedback.getUserId()
                    ).orElse(null);


            if (user == null) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error",
                                "User not found. Please login again."
                        ));
            }


            // ------------------------------------------
            // Validate Rating
            // ------------------------------------------

            if (feedback.getRating() == null) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error",
                                "Rating is required"
                        ));
            }


            if (
                    feedback.getRating() < 1 ||
                    feedback.getRating() > 5
            ) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error",
                                "Rating must be between 1 and 5"
                        ));
            }


            // ------------------------------------------
            // Validate Comment
            // ------------------------------------------

            if (
                    feedback.getComment() == null ||
                    feedback.getComment().isBlank()
            ) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error",
                                "Feedback comment is required"
                        ));
            }


            // ------------------------------------------
            // Set Created Time
            // ------------------------------------------

            feedback.setCreatedAt(
                    LocalDateTime.now()
            );


            // ------------------------------------------
            // Save Feedback
            // ------------------------------------------

            Feedback savedFeedback =
                    feedbackRepository.save(
                            feedback
                    );


            System.out.println(
                    "Feedback saved successfully!"
            );

            System.out.println(
                    "Feedback ID: " +
                    savedFeedback.getId()
            );


            // ------------------------------------------
            // Response
            // ------------------------------------------

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Feedback submitted successfully",

                            "feedbackId",
                            savedFeedback.getId(),

                            "userId",
                            savedFeedback.getUserId(),

                            "rating",
                            savedFeedback.getRating(),

                            "comment",
                            savedFeedback.getComment(),

                            "createdAt",
                            savedFeedback.getCreatedAt()
                    )
            );


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    "Failed to submit feedback",

                                    "details",
                                    e.getMessage()
                            )
                    );
        }
    }


    // ==========================================
    // GET FEEDBACK BY USER
    // ==========================================

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFeedbackByUser(
            @PathVariable Long userId) {

        try {

            List<Feedback> feedbackList =
                    feedbackRepository.findByUserId(
                            userId
                    );

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


    // ==========================================
    // GET ALL FEEDBACK
    // ==========================================

    @GetMapping
    public ResponseEntity<?> getAllFeedback() {

        try {

            return ResponseEntity.ok(
                    feedbackRepository.findAll()
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
}