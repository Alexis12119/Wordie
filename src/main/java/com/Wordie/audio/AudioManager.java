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
                try (InputStream is = getClass().getResourceAsStream("/audio/backgroundMusic.mp3")) {
                    if (is == null) break;
                    Player player = new Player(new BufferedInputStream(is));
                    bgPlayer = player;
                    try {
                        player.play();
                    } finally {
                        if (bgPlayer == player) {
                            bgPlayer = null;
                            player.close();
                        }
                    }
                } catch (JavaLayerException | IOException e) {
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
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is == null) return;
                Player player = new Player(new BufferedInputStream(is));
                fxPlayer = player;
                try {
                    player.play();
                } finally {
                    if (fxPlayer == player) {
                        fxPlayer = null;
                        player.close();
                    }
                }
            } catch (JavaLayerException | IOException e) {
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
