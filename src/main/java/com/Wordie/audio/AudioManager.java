package com.Wordie.audio;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class AudioManager {

    private Thread bgThread;
    private volatile boolean bgPlaying;
    private Thread fxThread;

    public void playBackground() {
        stopAll();
        bgPlaying = true;
        bgThread = new Thread(() -> {
            while (bgPlaying) {
                InputStream is = getClass().getResourceAsStream("/audio/backgroundMusic.mp3");
                if (is == null) break;
                try {
                    Player player = new Player(new BufferedInputStream(is));
                    player.play();
                    is.close();
                } catch (JavaLayerException | java.io.IOException e) {
                    System.err.println("Background audio error: " + e.getMessage());
                }
                if (Thread.interrupted()) break;
            }
        }, "bg-audio");
        bgThread.setDaemon(true);
        bgThread.start();
    }

    public void playWin() {
        playOnce("/audio/winnerMusic.mp3");
    }

    public void playLoss() {
        playOnce("/audio/gameOverMusic.mp3");
    }

    private void playOnce(String path) {
        stopEffect();
        fxThread = new Thread(() -> {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) return;
            try {
                Player player = new Player(new BufferedInputStream(is));
                player.play();
                is.close();
            } catch (JavaLayerException | java.io.IOException e) {
                System.err.println("Audio error: " + e.getMessage());
            }
        }, "fx-audio");
        fxThread.setDaemon(true);
        fxThread.start();
    }

    public void stopAll() {
        stopBackground();
        stopEffect();
    }

    public void stopBackground() {
        bgPlaying = false;
        if (bgThread != null) {
            bgThread.interrupt();
            bgThread = null;
        }
    }

    private void stopEffect() {
        if (fxThread != null) {
            fxThread.interrupt();
            fxThread = null;
        }
    }
}
