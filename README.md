♻️ AI-Powered Smart Waste Management System

An AI-powered web-based waste management system designed to simplify waste complaint reporting, automatically classify waste using machine learning, and support efficient complaint and cleanup-team management.

📌 Project Overview

The AI-Powered Smart Waste Management System allows users to report waste-related complaints by providing a description, image, and location information.

The uploaded waste image is analyzed by an AI model that predicts the waste category along with its prediction confidence. The complaint is then stored in a MySQL database for administrative management.

Administrators can manage complaints, search and filter complaints, assign available cleanup teams, monitor team availability, and track complaint completion.

✨ Key Features

- 🤖 AI-based waste classification from uploaded images
- 📊 AI prediction confidence display
- 📝 Waste complaint reporting with image upload
- 🔍 Complaint search by complaint ID
- 🔎 Complaint filtering by status
- 🖼️ Complaint image storage and preview
- 👷 Cleanup-team assignment
- 🔄 Cleanup-team BUSY/AVAILABLE workflow
- ⭐ User reward points for successful complaint reporting
- 🗄️ MySQL database integration
- 👨‍💼 Admin complaint management
- 📈 Waste-category statistics and analytics

🛠️ Technologies Used

Category| Technologies
Backend| Java, Spring Boot, Maven
AI & Machine Learning| Python, TensorFlow, Keras, Flask
Frontend| HTML, CSS, JavaScript
Database| MySQL
Version Control| Git, GitHub

🔄 System Workflow

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

📁 Project Structure

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

🤖 AI Waste Classification

The project uses a machine-learning model to classify uploaded waste images into different waste categories.

The AI service is implemented using Python, Flask, and TensorFlow/Keras.

AI Prediction Process

1. The user uploads a waste image.
2. The Spring Boot backend sends the image to the AI service.
3. The AI model analyzes the image.
4. The model predicts the most likely waste category.
5. The prediction confidence is calculated.
6. The category and confidence are returned to the backend.
7. The result is used while processing and storing the complaint.

♻️ Supported Waste Categories

The AI model supports 9 waste categories:

- Cardboard
- Paper
- Plastic
- Glass
- Metal
- Organic
- E-waste
- Textile
- Others

👨‍💼 Admin Management

The administrator can:

- View and manage waste complaints
- Search complaints using complaint ID
- Filter complaints according to their status
- View uploaded complaint images
- Assign available cleanup teams
- Monitor cleanup-team status
- Track complaint completion
- View waste-category statistics and analytics

👤 User Features

Users can:

- Register and log in to the system
- Report waste complaints
- Upload waste images
- Provide complaint information and location
- View AI-predicted waste category
- View AI prediction confidence
- Earn reward points for successful complaint reporting

👷 Cleanup-Team Workflow

The system manages cleanup teams using an availability-based workflow.

AVAILABLE
    ↓
Complaint Assigned
    ↓
BUSY
    ↓
Complaint Completed
    ↓
AVAILABLE

This workflow helps administrators assign complaints to available cleanup teams and monitor their current workload.

🚀 How to Run the Project

Prerequisites

Make sure the following are installed:

- Java
- Maven
- Python
- MySQL
- Git

1. Clone the Repository

git clone https://github.com/Nandini-Shende-241/AI-Powered-Smart-Waste-Management-System.git
cd AI-Powered-Smart-Waste-Management-System

2. Configure MySQL

Create the required MySQL database and configure the database connection in the Spring Boot application.

Update the database credentials according to your local MySQL setup.

3. Start the AI Service

Navigate to the AI model directory:

cd ai-model

Activate the Python virtual environment and install the required dependencies.

Start the Flask AI service:

python app.py

The AI service runs on:

http://127.0.0.1:5000

4. Start the Spring Boot Backend

Open another terminal and navigate to the backend:

cd backend

Run the Spring Boot application:

mvn spring-boot:run

The backend runs on:

http://localhost:8080

5. Open the Application

Open the frontend in your browser and use the application according to your user or administrator role.

«Note: Database credentials and other environment-specific configuration should be updated according to the local development environment.»

👩‍💻 My Role

Project Group Leader

- Coordinated project activities and task distribution among team members.
- Contributed to backend development and AI integration.
- Worked on database functionality and complaint management features.
- Contributed to cleanup-team workflow implementation.
- Participated in project testing, debugging, and system integration.

🔮 Future Scope

- Improve AI classification accuracy using a larger and more diverse dataset.
- Add advanced AI-based waste insights and recommendations.
- Expand analytics and reporting capabilities.
- Further improve location-based waste management and monitoring.
- Enhance the system with additional smart waste-management features.

📊 Project Status

The core waste complaint reporting, AI classification, complaint management, cleanup-team workflow, and waste-category analytics features have been implemented and tested.

👩‍🎓 Author

Nandini Anil Shende

B.Tech Information Technology
Kavikulguru Institute of Technology and Science, Ramtek
Nagpur, Maharashtra, India

🔗 GitHub: "Nandini-Shende-241" (https://github.com/Nandini-Shende-241)


## Screenshots

### 1. User Login
![User Login](screenshots/User%20login.jpg)

### 2. Report Waste Form
![Report Waste Form](screenshots/Report%20waste%20form.jpg)

### 3. Report Waste Processing
![Report Waste Processing](screenshots/Report%20waste%20processing.jpg)

### 4. Report Successfully Submitted
![Report Successfully Submitted](screenshots/Report%20successfully%20submitted.jpg)

### 5. User Dashboard
![User Dashboard](screenshots/User%20Dashboard.jpg)

### 6. Admin Dashboard – AI Insights
![Admin Dashboard](screenshots/Admin%20Dashboard.jpg)

### 7. Admin Dashboard – Analytics
![Admin Dashboard Analytics](screenshots/Admin%20Dashboard%20Analytics.jpg)
