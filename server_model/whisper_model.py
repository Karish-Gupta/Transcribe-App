import librosa
import torch, torchaudio
from transformers import WhisperProcessor, WhisperForConditionalGeneration

# Load modela and processor 
processor = WhisperProcessor.from_pretrained("openai/whisper-tiny")
model = WhisperForConditionalGeneration.from_pretrained("openai/whisper-tiny")

model.eval()

# Load audio as mono waveform (1d array) and resample to 16kHz
audio, sr = librosa.load("data/test_audio.wav", sr=16000, mono=True)

# Convert to tensor and add batch dimension
input_tensor = torch.tensor(audio)

# Preprocess
inputs = processor(input_tensor, sampling_rate=16000, return_tensors="pt")

# Generate transcription 
with torch.no_grad():
   predicted_ids = model.generate(inputs["input_features"])

# Decode
transcription = processor.batch_decode(predicted_ids, skip_special_tokens=True)[0]
print(transcription)