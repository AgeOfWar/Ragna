package com.github.ageofwar.solex.example;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SoundPlayer implements Runnable {
    private static final float SAMPLE_RATE = 44100f;
    private static final int BUFFER_SIZE = 512;
    private static final double VOLUME = 0.25;

    private volatile boolean running = false;
    private Thread thread;

    public void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "EngineSoundThread");
        thread.start();
    }

    public void stop() {
        running = false;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {}
        }
    }

    @Override
    public void run() {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);

        try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
            line.open(format, BUFFER_SIZE);
            line.start();

            byte[] buffer = new byte[BUFFER_SIZE];
            double phase = 0.0;
            double vibPhase = 0.0;

            while (running) {
                for (int i = 0; i < BUFFER_SIZE; i++) {
                    double baseFreq = 100.0;
                    double vibFreq = 5.0;
                    double vibrato = Math.sin(2 * Math.PI * vibPhase) * 5.0;

                    double sample = Math.sin(2 * Math.PI * phase) * VOLUME;
                    buffer[i] = (byte) (sample * 127);

                    phase = (phase + (baseFreq + vibrato) / SAMPLE_RATE) % 1.0;
                    vibPhase = (vibPhase + vibFreq / SAMPLE_RATE) % 1.0;
                }

                line.write(buffer, 0, BUFFER_SIZE);
            }

            line.drain();
            line.stop();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}