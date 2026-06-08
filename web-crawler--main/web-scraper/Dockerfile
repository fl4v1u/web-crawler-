# Wir starten mit dem offiziellen Ollama-Image
FROM ollama/ollama:latest

# Wir starten kurz Ollama im Hintergrund, laden das Modell herunter und beenden es wieder,
# damit das Modell fest im Docker-Image eingebrannt ist.
RUN ollama serve & \
    sleep 5 && \
    ollama pull gemma4:latest

# Port freigeben (wie in deinem Docker Desktop zu sehen)
EXPOSE 11434