package net.mcreator.morebosses.client.tool;

public class ControlledAnimation {
    private int timer = 0;
    private int maxTimer;
    
    public ControlledAnimation(int max) {
        this.maxTimer = max;
    }
    
    public void increaseTimer() {
        if (timer < maxTimer) timer++;
    }
    
    public void decreaseTimer() {
        if (timer > 0) timer--;
    }
    
    public int getTimer() {
        return timer;
    }
}