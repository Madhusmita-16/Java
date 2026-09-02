package com.safety.service;

import javax.sound.sampled.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioSirenService {

    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private Thread sirenThread;

    public synchronized void startSiren() {
        if (isPlaying.get()) return;
        isPlaying.set(true);

        sirenThread = new Thread(() -> {
            try {
                AudioFormat format = new AudioFormat(44100, 8, 1, true, false);
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format, 44100);
                line.start();

                byte[] buffer = new byte[44100];
                double cycle = 0;

                while (isPlaying.get()) {
                    // Oscillate frequency between 600 Hz and 1400 Hz (Emergency Siren pattern)
                    for (int i = 0; i < buffer.length && isPlaying.get(); i++) {
                        double freq = 600 + 800 * Math.sin(2 * Math.PI * (i / 44100.0) * 2);
                        cycle += freq / 44100.0;
                        buffer[i] = (byte) (Math.sin(2 * Math.PI * cycle) * 127);
                    }
                    line.write(buffer, 0, buffer.length);
                }

                line.drain();
                line.close();
            } catch (Exception e) {
                System.err.println("Siren audio simulation error: " + e.getMessage());
            }
        });

        sirenThread.setDaemon(true);
        sirenThread.start();
    }

    public synchronized void stopSiren() {
        isPlaying.set(false);
        if (sirenThread != null) {
            sirenThread.interrupt();
            sirenThread = null;
        }
    }

    public boolean isSirenActive() {
        return isPlaying.get();
    }
}
