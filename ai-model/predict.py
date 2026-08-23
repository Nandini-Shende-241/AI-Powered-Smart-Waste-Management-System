import os
import json
import numpy as np
import tensorflow as tf
from tensorflow.keras.utils import load_img, img_to_array

# --------------------------------------------------
# 1. PROJECT PATHS
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
# 2. LOAD TRAINED MODEL
# --------------------------------------------------

print("Loading trained model...")

model = tf.keras.models.load_model(MODEL_PATH)

print("Model loaded successfully.")

# --------------------------------------------------
# 3. LOAD CLASS NAMES
# --------------------------------------------------

with open(CLASS_NAMES_PATH, "r") as f:
    class_names = json.load(f)

print("Classes:", class_names)

# --------------------------------------------------
# 4. ASK FOR IMAGE
# --------------------------------------------------

image_path = input(
    "\nEnter the full path of the waste image: "
).strip().strip('"')

# Check whether image exists
if not os.path.exists(image_path):
    print("\nImage not found.")
    print("Please check the image path.")
    exit()

# --------------------------------------------------
# 5. LOAD IMAGE
# --------------------------------------------------

img = load_img(
    image_path,
    target_size=IMAGE_SIZE
)

img_array = img_to_array(img)

img_array = np.expand_dims(
    img_array,
    axis=0
)

# --------------------------------------------------
# IMPORTANT:
# Do NOT use MobileNetV2 preprocess_input here.
# The trained model already contains preprocessing.
# --------------------------------------------------

# --------------------------------------------------
# 6. MAKE PREDICTION
# --------------------------------------------------

predictions = model.predict(
    img_array,
    verbose=0
)

predicted_index = np.argmax(
    predictions[0]
)

predicted_class = class_names[
    predicted_index
]

confidence = (
    predictions[0][predicted_index] * 100
)

# --------------------------------------------------
# 7. DISPLAY RESULT
# --------------------------------------------------

print("\n--------------------------------")
print("AI WASTE CLASSIFICATION RESULT")
print("--------------------------------")

print(
    "Waste Type:",
    predicted_class
)

print(
    "Confidence: {:.2f}%".format(
        confidence
    )
)

print("--------------------------------")