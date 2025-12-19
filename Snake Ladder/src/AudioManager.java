import javax.sound.sampled.*;
import java.io.File;

class AudioManager {
    private Clip bgmClip;
    private int volumeLevel = 80; // Default 80%

    public void playBGM(SoundChoice choice) {
        stopBGM();
        if (choice == SoundChoice.NO_SOUND) return;

        String fileName = (choice == SoundChoice.MINECRAFT_1)
                ? "gaming-game-minecraft-background-music-372242.wav"
                : "game-gaming-minecraft-background-music-377647 (2).wav";

        try {
            File f = new File(fileName);
            if (f.exists()) {
                AudioInputStream stream = AudioSystem.getAudioInputStream(f);
                bgmClip = AudioSystem.getClip();
                bgmClip.open(stream);
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
                applyVolume(bgmClip);
                bgmClip.start();
            }
        } catch (Exception e) {
            System.err.println("Audio Error: " + e.getMessage());
        }
    }

    public void playSFX(String fileName) {
        if (volumeLevel <= 0) return;
        try {
            File f = new File(fileName);
            if (f.exists()) {
                AudioInputStream stream = AudioSystem.getAudioInputStream(f);
                Clip sfx = AudioSystem.getClip();
                sfx.open(stream);
                applyVolume(sfx);
                sfx.start();
            }
        } catch (Exception ignored) {}
    }

    public void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
    }

    public void setVolume(int volume) {
        this.volumeLevel = volume;
        applyVolume(bgmClip);
    }

    public int getVolume() {
        return volumeLevel;
    }

    private void applyVolume(Clip clip) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float vol = volumeLevel / 100.0f;
            if (vol <= 0.0f) vol = 0.0001f;
            float dB = (float) (Math.log(vol) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
}