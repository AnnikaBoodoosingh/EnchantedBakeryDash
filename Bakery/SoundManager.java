import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;  // For storing sound clips

public class SoundManager {
    private HashMap<String, Clip> clips;  // Store loaded sound clips
    private static SoundManager instance = null;  // Singleton instance

    private SoundManager() {
        clips = new HashMap<String, Clip>();
        // Example: Load the jazz.wav sound clip during initialization
        loadClip("jazz.wav");
    }

    // Singleton method to get the instance of SoundManager
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    // Load a clip from the specified file and store it in the clips map
    public void loadClip(String fileName) {
        try {
            // Check if clip is already loaded
            if (!clips.containsKey(fileName)) {
                File soundFile = new File(fileName);
                if (soundFile.exists()) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clips.put(fileName, clip);  // Store the clip for future use
                } else {
                    System.out.println("File not found: " + fileName);
                }
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error loading sound file " + fileName + ": " + e);
        }
    }

    // Retrieve a clip by its title
    public Clip getClip(String title) {
        return clips.get(title);
    }

    // Play the specified clip, either looping or not
    public void playClip(String title, boolean looping) {
        Clip clip = getClip(title);
        if (clip != null) {
            clip.setFramePosition(0);  // Rewind the clip to the beginning
            if (looping) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);  // Loop the clip indefinitely
            } else {
                clip.start();  // Play the clip once
            }
        } else {
            System.out.println("Clip not found: " + title);
        }
    }

    // Stop the specified clip
    public void stopClip(String title) {
        Clip clip = getClip(title);
        if (clip != null) {
            clip.stop();
        } else {
            System.out.println("Clip not found: " + title);
        }
    }

    // Set the volume of a clip
    public void setVolume(String title, float volume) {
        Clip clip = getClip(title);
        if (clip != null) {
            // Access the volume control (gain control)
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            if (gainControl != null) {
                // Calculate the gain level
                float range = gainControl.getMaximum() - gainControl.getMinimum();
                float gain = (range * volume) + gainControl.getMinimum();
                gainControl.setValue(gain);  // Set the volume level
            }
        } else {
            System.out.println("Clip not found for volume adjustment: " + title);
        }
    }
}
