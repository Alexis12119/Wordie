package com.Wordie.audio;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class AudioManager {

    private Thread bgThread;
    private volatile Player bgPlayer;

    private Thread fxThread;
    private volatile Player fxPlayer;

    public synchronized void playBackground() {
        stopBackground();
        bgThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                InputStream is = getClass().getResourceAsStream("/audio/backgroundMusic.mp3");
                if (is == null) break;
                try {
                    Player player = new Player(new BufferedInputStream(is));
                    bgPlayer = player;
                    player.play();
                    bgPlayer = null;
                    is.close();
                } catch (JavaLayerException | java.io.IOException e) {
                    System.err.println("Background audio error: " + e.getMessage());
                    break;
                }
            }
        }, "bg-audio");
        bgThread.setDaemon(true);
        bgThread.start();
    }

    public synchronized void playWin() {
        playOnce("/audio/winnerMusic.mp3");
    }

    public synchronized void playLoss() {
        playOnce("/audio/gameOverMusic.mp3");
    }

    private void playOnce(String path) {
        stopEffect();
        fxThread = new Thread(() -> {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) return;
            try {
                Player player = new Player(new BufferedInputStream(is));
                fxPlayer = player;
                player.play();
                fxPlayer = null;
                is.close();
            } catch (JavaLayerException | java.io.IOException e) {
                System.err.println("Audio error: " + e.getMessage());
            }
        }, "fx-audio");
        fxThread.setDaemon(true);
        fxThread.start();
    }

    public synchronized void stopAll() {
        stopBackground();
        stopEffect();
    }

    public synchronized void stopBackground() {
        if (bgPlayer != null) {
            bgPlayer.close();
            bgPlayer = null;
        }
        if (bgThread != null) {
            bgThread.interrupt();
            bgThread = null;
        }
    }

    private synchronized void stopEffect() {
        if (fxPlayer != null) {
            fxPlayer.close();
            fxPlayer = null;
        }
        if (fxThread != null) {
            fxThread.interrupt();
            fxThread = null;
        }
    }
}
