package com.example.spotter.Model;

public class SensorData {

    private boolean startTransfer;
    private boolean stopTransfer;
    private int delayTime;

    public SensorData() {
        // Default constructor required for Firebase Realtime Database
    }

    public SensorData(boolean startTransfer, boolean stopTransfer, int delayTime) {
        this.startTransfer = startTransfer;
        this.stopTransfer = stopTransfer;
        this.delayTime = delayTime;
    }

    public boolean isStartTransfer() {
        return startTransfer;
    }

    public void setStartTransfer(boolean startTransfer) {
        this.startTransfer = startTransfer;
    }

    public boolean isStopTransfer() {
        return stopTransfer;
    }

    public void setStopTransfer(boolean stopTransfer) {
        this.stopTransfer = stopTransfer;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    // Add getters and setters if needed
}
