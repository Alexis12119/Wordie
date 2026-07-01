package com.Wordie.audio;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioManager {

    private Thread bgThread;
    private Player bgPlayer;

    private Thread fxThread;
    private Player fxPlayer;

    public synchronized void playBackground() {
        stopBackground();
        bgThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try (InputStream audioStream = getClass().getResourceAsStream("/audio/backgroundMusic.mp3")) {
                    if (audioStream == null) break;
                    Player player = new Player(new BufferedInputStream(audioStream));
                    bgPlayer = player;
                    try {
                        player.play();
                    } finally {
                        if (bgPlayer == player) {
                            bgPlayer = null;
                            player.close();
                        }
                    }
                } catch (JavaLayerException | IOException event) {
                    System.err.println("Background audio error: " + event.getMessage());
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
            try (InputStream audioStream = getClass().getResourceAsStream(path)) {
                if (audioStream == null) return;
                Player player = new Player(new BufferedInputStream(audioStream));
                fxPlayer = player;
                try {
                    player.play();
                } finally {
                    if (fxPlayer == player) {
                        fxPlayer = null;
                        player.close();
                    }
                }
            } catch (JavaLayerException | IOException event) {
                System.err.println("Audio error: " + event.getMessage());
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
