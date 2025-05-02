import os
from flask import Flask, request, jsonify
import torch
import signal, sys
from transformers import WhisperProcessor, WhisperForConditionalGeneration
import librosa
import traceback

# Initialize the Flask app
app = Flask(__name__)

# Load the pretrained Whisper-tiny model by openAI
processor = WhisperProcessor.from_pretrained("openai/whisper-tiny")
model = WhisperForConditionalGeneration.from_pretrained("openai/whisper-tiny")
model.eval()

# predicy POST route
@app.route('/predict', methods=['POST'])
def predict():
    try:
        # Check if an audio file is present in the request
        if 'file' not in request.files:
            return jsonify({"error": "No file part in the request"}), 400

        file = request.files['file']
        if file.filename == '':
            return jsonify({"error": "No selected file"}), 400

        # Save audio to uploads folder
        file.save(os.path.join('uploads', file.filename))
        
        # Load audio as mono waveform (1d array) and resample to 16kHz
        audio, sr = librosa.load(f"uploads/{file.filename}", sr=16000, mono=True)

        # Convert to tensor and add batch dimension
        # input_tensor = torch.tensor(audio)

        # Preprocess
        inputs = processor(audio, sampling_rate=16000, return_tensors="pt")

        # Generate transcription 
        with torch.no_grad():
            predicted_ids = model.generate(inputs["input_features"])

        # Decode
        transcription = processor.batch_decode(predicted_ids, skip_special_tokens=True)[0]

        # The API now returns predicted transcription
        return jsonify({"transcription": transcription})  # HTTP 200

    except Exception as e:
        traceback.print_exc()  # Debug
        return jsonify({"error": str(e)}), 500

def handle_exit(signal, frame):
    print("Shutting down gracefully...")
    sys.exit(0)

if __name__ == '__main__':
    signal.signal(signal.SIGINT, handle_exit)
    signal.signal(signal.SIGTERM, handle_exit)
    app.run(host='0.0.0.0', port=5050, threaded=True)