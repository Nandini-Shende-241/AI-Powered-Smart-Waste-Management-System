import os
import json
import tensorflow as tf
from tensorflow.keras import callbacks
from tensorflow.keras.preprocessing import image_dataset_from_directory

# --------------------------------------------------
# 1. PROJECT PATHS
# --------------------------------------------------

BASE_DIR = r"C:\Users\shend\OneDrive\Desktop\AI-Smart_Waste_Management"

DATASET_DIR = os.path.join(BASE_DIR, "dataset")
MODEL_DIR = os.path.join(BASE_DIR, "ai-model")

OLD_MODEL_PATH = os.path.join(MODEL_DIR, "waste_classifier.keras")
NEW_MODEL_PATH = os.path.join(MODEL_DIR, "waste_classifier_finetuned.keras")
CLASS_NAMES_PATH = os.path.join(MODEL_DIR, "class_names.json")

# --------------------------------------------------
# 2. SETTINGS
# --------------------------------------------------

IMAGE_SIZE = (224, 224)
BATCH_SIZE = 32
SEED = 42
EPOCHS = 15

# --------------------------------------------------
# 3. LOAD THE EXISTING TRAINED MODEL
# --------------------------------------------------

print("Loading existing trained model...")

model = tf.keras.models.load_model(OLD_MODEL_PATH)

print("Existing model loaded successfully.")

# --------------------------------------------------
# 4. LOAD DATASET
# --------------------------------------------------

print("\nLoading dataset...")

train_dataset = image_dataset_from_directory(
    DATASET_DIR,
    validation_split=0.2,
    subset="training",
    seed=SEED,
    image_size=IMAGE_SIZE,
    batch_size=BATCH_SIZE
)

validation_dataset = image_dataset_from_directory(
    DATASET_DIR,
    validation_split=0.2,
    subset="validation",
    seed=SEED,
    image_size=IMAGE_SIZE,
    batch_size=BATCH_SIZE
)

class_names = train_dataset.class_names

print("\nClasses:")
for i, name in enumerate(class_names):
    print(i, ":", name)

# Save class names
with open(CLASS_NAMES_PATH, "w") as f:
    json.dump(class_names, f)

# --------------------------------------------------
# 5. FIND MOBILENETV2 BASE MODEL
# --------------------------------------------------

base_model = None

for layer in model.layers:
    if isinstance(layer, tf.keras.Model) and "mobilenet" in layer.name.lower():
        base_model = layer
        break

if base_model is None:
    raise RuntimeError("MobileNetV2 base model was not found.")

print("\nFound base model:", base_model.name)

# --------------------------------------------------
# 6. FREEZE ALL BASE MODEL LAYERS
# --------------------------------------------------

base_model.trainable = True

for layer in base_model.layers:
    layer.trainable = False

# --------------------------------------------------
# 7. FINE-TUNE LAST 30 LAYERS
# --------------------------------------------------

for layer in base_model.layers[-30:]:
    if not isinstance(layer, tf.keras.layers.BatchNormalization):
        layer.trainable = True

print("\nFine-tuning the last 30 MobileNetV2 layers.")

# --------------------------------------------------
# 8. DATA PIPELINE
# --------------------------------------------------

AUTOTUNE = tf.data.AUTOTUNE

train_dataset = train_dataset.prefetch(AUTOTUNE)
validation_dataset = validation_dataset.prefetch(AUTOTUNE)

# --------------------------------------------------
# 9. CLASS WEIGHTS
# --------------------------------------------------

image_counts = {
    "cardboard": 893,
    "e-waste": 993,
    "glass": 948,
    "metal": 901,
    "organic": 967,
    "other": 976,
    "paper": 1377,
    "plastic": 891,
    "textile": 985
}

total_images = sum(image_counts.values())
num_classes = len(class_names)

class_weights = {}

for index, class_name in enumerate(class_names):
    count = image_counts[class_name]
    class_weights[index] = total_images / (num_classes * count)

print("\nClass weights:")
for index, weight in class_weights.items():
    print(class_names[index], ":", round(weight, 3))

# --------------------------------------------------
# 10. COMPILE MODEL
# --------------------------------------------------

model.compile(
    optimizer=tf.keras.optimizers.Adam(
        learning_rate=0.00001
    ),
    loss="sparse_categorical_crossentropy",
    metrics=["accuracy"]
)

# --------------------------------------------------
# 11. CALLBACKS
# --------------------------------------------------

checkpoint = callbacks.ModelCheckpoint(
    NEW_MODEL_PATH,
    monitor="val_accuracy",
    save_best_only=True,
    mode="max",
    verbose=1
)

early_stopping = callbacks.EarlyStopping(
    monitor="val_accuracy",
    patience=4,
    mode="max",
    restore_best_weights=True,
    verbose=1
)

# --------------------------------------------------
# 12. FINE-TUNE MODEL
# --------------------------------------------------

print("\nStarting fine-tuning...\n")

history = model.fit(
    train_dataset,
    validation_data=validation_dataset,
    epochs=EPOCHS,
    class_weight=class_weights,
    callbacks=[
        checkpoint,
        early_stopping
    ]
)

# --------------------------------------------------
# 13. SAVE FINAL MODEL
# --------------------------------------------------

model.save(NEW_MODEL_PATH)

print("\n========================================")
print("FINE-TUNING COMPLETED SUCCESSFULLY!")
print("========================================")

print("\nBest model saved at:")
print(NEW_MODEL_PATH)

print("\nClass names saved at:")
print(CLASS_NAMES_PATH)