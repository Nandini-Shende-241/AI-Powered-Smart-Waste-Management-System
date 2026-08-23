import os
import json
import io
import numpy as np
import tensorflow as tf

from flask import Flask, request, jsonify
from PIL import Image


# --------------------------------------------------
# PROJECT PATHS
# --------------------------------------------------

BASE_DIR = r"C:\Users\shend\OneDrive\Desktop\AI-Smart_Waste_Management"

MODEL_PATH = os.path.join(
    BASE_DIR,
    "ai-model",
    "waste_classifier_finetuned.keras"
)

CLASS_NAMES_PATH = os.path.join(
    BASE_DIR,
    "ai-model",
    "class_names.json"
)

IMAGE_SIZE = (224, 224)


# --------------------------------------------------
# LOAD MODEL
# --------------------------------------------------

print("Loading AI model...")

model = tf.keras.models.load_model(MODEL_PATH)

print("AI model loaded successfully.")


# --------------------------------------------------
# LOAD CLASS NAMES
# --------------------------------------------------

with open(CLASS_NAMES_PATH, "r") as f:
    class_names = json.load(f)

print("Classes:", class_names)


# --------------------------------------------------
# CREATE FLASK APP
# --------------------------------------------------

app = Flask(__name__)


# --------------------------------------------------
# HEALTH CHECK
# --------------------------------------------------

@app.get("/health")
def health():

    return jsonify({
        "status": "AI service is running"
    })


# --------------------------------------------------
# PREDICT WASTE
# --------------------------------------------------

@app.post("/predict")
def predict():

    try:

        # Check image
        if "image" not in request.files:
            return jsonify({
                "error": "No image received"
            }), 400

        image_file = request.files["image"]

        if image_file.filename == "":
            return jsonify({
                "error": "No image selected"
            }), 400

        # --------------------------------------------------
        # READ UPLOADED IMAGE AS BYTES
        # --------------------------------------------------

        image_bytes = image_file.read()

        if not image_bytes:
            return jsonify({
                "error": "Uploaded image is empty"
            }), 400

        # --------------------------------------------------
        # OPEN IMAGE FROM BYTES
        # --------------------------------------------------

        image = Image.open(
            io.BytesIO(image_bytes)
        )

        # Convert image to RGB
        image = image.convert("RGB")

        # Resize image
        image = image.resize(IMAGE_SIZE)

        # Convert image to numpy array
        img_array = np.array(image)

        # Add batch dimension
        img_array = np.expand_dims(
            img_array,
            axis=0
        )

        # --------------------------------------------------
        # AI PREDICTION
        # --------------------------------------------------

        predictions = model.predict(
            img_array,
            verbose=0
        )

        predicted_index = int(
            np.argmax(predictions[0])
        )

        predicted_class = class_names[
            predicted_index
        ]

        confidence = float(
            predictions[0][predicted_index] * 100
        )

        # --------------------------------------------------
        # RETURN RESULT
        # --------------------------------------------------

        return jsonify({
            "wasteType": predicted_class,
            "confidence": round(confidence, 2)
        })

    except Exception as e:

        print("Prediction error:", str(e))

        return jsonify({
            "error": str(e)
        }), 500


# --------------------------------------------------
# START SERVER
# --------------------------------------------------

if __name__ == "__main__":

    print("")
    print("====================================")
    print("AI WASTE CLASSIFICATION SERVICE")
    print("====================================")
    print("Running at http://127.0.0.1:5000")
    print("====================================")
    print("")

    app.run(
        host="127.0.0.1",
        port=5000,
        debug=False
    )