import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    private Clip backgroundMusic;
    private Clip pathSound;
    private Clip completeSound;
    private boolean soundEnabled;
    private float volume = 0.5f; // 0.0 to 1.0

    public SoundManager(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public void playBackgroundMusic(String filepath) {
        if (!soundEnabled) return;

        try {
            stopBackgroundMusic();
            File audioFile = new File(filepath);

            if (!audioFile.exists()) {
                System.err.println("Audio file not found: " + audioFile.getAbsolutePath());
                return;
            }

            System.out.println("Loading background music: " + audioFile.getAbsolutePath());
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            setVolume(backgroundMusic, volume);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
            System.out.println("Background music started successfully!");
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio format: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable: " + e.getMessage());
        }
    }

    public void playPathSound(String filepath) {
        if (!soundEnabled) return;

        try {
            File audioFile = new File(filepath);
            if (!audioFile.exists()) {
                System.err.println("Step audio file not found: " + audioFile.getAbsolutePath());
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            pathSound = AudioSystem.getClip();
            pathSound.open(audioStream);
            setVolume(pathSound, volume);
            pathSound.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing path sound: " + e.getMessage());
        }
    }

    public void playCompleteSound(String filepath) {
        if (!soundEnabled) return;

        try {
            File audioFile = new File(filepath);
            if (!audioFile.exists()) {
                System.err.println("Complete audio file not found: " + audioFile.getAbsolutePath());
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            completeSound = AudioSystem.getClip();
            completeSound.open(audioStream);
            setVolume(completeSound, volume);
            completeSound.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing complete sound: " + e.getMessage());
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
    }

    public void setVolume(float newVolume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, newVolume));

        if (backgroundMusic != null && backgroundMusic.isOpen()) {
            setVolume(backgroundMusic, volume);
        }
    }

    private void setVolume(Clip clip, float volume) {
        if (clip != null && clip.isOpen()) {
            try {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float range = gainControl.getMaximum() - gainControl.getMinimum();
                float gain = (range * volume) + gainControl.getMinimum();
                gainControl.setValue(gain);
            } catch (Exception e) {
                System.err.println("Error setting volume: " + e.getMessage());
            }
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public float getVolume() {
        return volume;
    }

    public void dispose() {
        stopBackgroundMusic();
        if (pathSound != null) pathSound.close();
        if (completeSound != null) completeSound.close();
    }
}