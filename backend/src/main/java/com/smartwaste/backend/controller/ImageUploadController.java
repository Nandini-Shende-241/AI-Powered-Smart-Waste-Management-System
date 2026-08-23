package com.smartwaste.backend.controller;

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
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
@CrossOrigin
public class ImageUploadController {

    private final Path uploadDirectory = Paths.get("uploads");

    private final RestClient restClient = RestClient.create();

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @RequestParam("image") MultipartFile image) {

        try {

            // Check whether an image was selected
            if (image.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("No image selected.");
            }

            // Create uploads folder if it does not exist
            Files.createDirectories(uploadDirectory);

            // Get original filename
            String originalFileName = image.getOriginalFilename();

            String fileExtension = "";

            if (originalFileName != null &&
                    originalFileName.contains(".")) {

                fileExtension =
                        originalFileName.substring(
                                originalFileName.lastIndexOf("."));
            }

            // Create unique filename
            String fileName =
                    UUID.randomUUID().toString()
                            + fileExtension;

            // Save image
            Path filePath =
                    uploadDirectory.resolve(fileName);

            Files.copy(
                    image.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Prepare image for Flask AI service
            ByteArrayResource imageResource =
                    new ByteArrayResource(image.getBytes()) {

                        @Override
                        public String getFilename() {
                            return originalFileName != null
                                    ? originalFileName
                                    : "image.jpg";
                        }
                    };

            // Create multipart request
            MultiValueMap<String, Object> body =
                    new LinkedMultiValueMap<>();

            body.add("image", imageResource);

            // Send image to Flask AI service
            String aiResult =
                    restClient.post()
                            .uri("http://127.0.0.1:5000/predict")
                            .contentType(
                                    MediaType.MULTIPART_FORM_DATA)
                            .body(body)
                            .retrieve()
                            .body(String.class);

            // Return AI result
            return ResponseEntity.ok(
                    aiResult
            );

        } catch (IOException e) {

            return ResponseEntity.internalServerError()
                    .body("Failed to upload image: "
                            + e.getMessage());

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body("AI service error: "
                            + e.getMessage());
        }
    }
}