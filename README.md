# AI-Powered Smart Waste Management System

An AI-powered web-based waste management system designed to simplify waste complaint reporting, automatically classify waste using machine learning, and support efficient complaint and cleanup-team management.

## Project Overview

The AI-Powered Smart Waste Management System allows users to report waste-related complaints by providing a description, image, and location information.

The uploaded waste image is analyzed by an AI model that predicts the waste category along with its prediction confidence. The complaint is then stored in the MySQL database for administrative management.

Administrators can manage complaints, search and filter complaints, assign available cleanup teams, monitor team availability, and track complaint completion.

## Key Features

- AI-based waste classification from uploaded images
- AI prediction confidence display
- Waste complaint reporting with image upload
- Complaint search by complaint ID
- Complaint filtering by status
- Complaint image storage and preview
- Cleanup-team assignment
- Cleanup-team BUSY/AVAILABLE workflow
- User reward points for successful complaint reporting
- MySQL database integration
- Admin complaint management
- Waste-category statistics and analytics

## Technologies Used

### Backend

- Java
- Spring Boot
- Maven

### AI & Machine Learning

- Python
- TensorFlow / Keras
- Machine Learning
- Flask

### Frontend

- HTML
- CSS
- JavaScript

### Database

- MySQL

### Tools & Version Control

- Git
- GitHub

## System Workflow

```text
User Reports Waste Complaint
            ↓
     Uploads Waste Image
            ↓
       AI Model Analysis
            ↓
 Waste Category + Confidence
            ↓
   Complaint Stored in MySQL
            ↓
      Admin Reviews Complaint
            ↓
   Cleanup Team is Assigned
            ↓
       Team Status: BUSY
            ↓
      Complaint Completed
            ↓
     Team Status: AVAILABLE



##Project Structure

AI-Powered-Smart-Waste-Management-System/
│
├── ai-model/
│   ├── train_model.py
│   ├── waste_classifier.keras
│   ├── class_names.json
│   └── venv/
│
├── backend/
│   ├── src/
│   ├── uploads/
│   ├── pom.xml
│   └── ...
│
├── .gitignore
└── README.md
