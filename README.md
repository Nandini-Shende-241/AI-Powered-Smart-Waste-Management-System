AI-Powered Smart Waste Management System

An AI-powered web-based waste management system designed to simplify waste complaint reporting, automatically classify waste using machine learning, and support efficient complaint and cleanup-team management.

Project Overview

The system allows users to report waste-related complaints by providing a description and an image of the waste. The uploaded image is analyzed by an AI model, which predicts the waste category along with its prediction confidence.

Administrators can manage complaints, search and filter complaints, assign cleanup teams, and monitor the cleanup workflow.

Key Features

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

Technologies Used

Backend

- Java
- Spring Boot
- Maven

AI and Machine Learning

- Python
- Machine Learning
- Flask

Frontend

- HTML
- CSS
- JavaScript

Database

- MySQL

Tools & Version Control

- Git
- GitHub

System Workflow

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

Project Structure

AI-Powered-Smart-Waste-Management-System/
│
├── ai-model/
│   └── AI waste classification files
│
├── backend/
│   └── Spring Boot backend application
│
├── .gitignore
└── README.md

AI Waste Classification

The project uses a machine-learning model to classify uploaded waste images into different waste categories.

The AI service is implemented using Python and Flask. The backend sends the uploaded image to the AI service, receives the predicted waste category and confidence score, and uses the result while processing the complaint.

The model supports waste categories including:

- Cardboard
- Paper
- Plastic
- Glass
- Metal
- Organic
- E-waste
- Textile

Admin Management

The administrator can:

- View and manage waste complaints
- Search complaints using complaint ID
- Filter complaints according to their status
- View uploaded complaint images
- Assign available cleanup teams
- Monitor cleanup-team status
- Track complaint completion
- View waste-category statistics and analytics

User Features

Users can:

- Register and log in to the system
- Report waste complaints
- Upload waste images
- View AI-predicted waste category
- View AI prediction confidence
- Earn reward points for successful complaint reporting

My Role

Project Group Leader

- Coordinated project activities and task distribution among team members.
- Contributed to backend development and AI integration.
- Worked on database functionality and complaint management features.
- Contributed to cleanup-team workflow implementation.
- Participated in project testing and integration.

Future Scope

- Improve AI classification accuracy using a larger and more diverse dataset.
- Add advanced AI-based waste insights and recommendations.
- Expand analytics and reporting capabilities.
- Further improve location-based waste management and monitoring.
- Enhance the system with additional smart waste-management features.

Project Status

The core waste complaint reporting, AI classification, complaint management, cleanup-team workflow, and analytics features have been implemented and tested.

Author

Nandini Anil Shende

B.Tech Information Technology
Kavikulguru Institute of Technology and Science, Ramtek