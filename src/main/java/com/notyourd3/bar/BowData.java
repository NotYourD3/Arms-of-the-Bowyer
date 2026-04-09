package com.notyourd3.bar;

/**
 * pullback
 * zoom
 * range
 * damage
 */
public class BowData {
    float pullbackMultiplier;
    float zoomMultiplier;
    float rangeMultiplier;
    float damageMultiplier;

    public BowData(String[] parts) {
        if (parts.length == 5) {
            this.pullbackMultiplier = parseFloat(parts[1]);
            this.zoomMultiplier = parseFloat(parts[2]);
            this.rangeMultiplier = parseFloat(parts[3]);
            this.damageMultiplier = parseFloat(parts[4]);
        }

    }

    private float parseFloat(String s) {
        try {
            return Float.parseFloat(s.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid float value: '" + s + "'", e);
        }
    }

    public BowData(float pullbackMultiplier, float zoomMultiplier, float rangeMultiplier, float damageMultiplier) {
        this.pullbackMultiplier = pullbackMultiplier;
        this.zoomMultiplier = zoomMultiplier;
        this.rangeMultiplier = rangeMultiplier;
        this.damageMultiplier = damageMultiplier;
    }
    //getter
    public float getPullbackMultiplier() {
        return pullbackMultiplier;
    }
    public float getZoomMultiplier() {
        return zoomMultiplier;
    }
    public float getRangeMultiplier() {
        return rangeMultiplier;
    }
    public float getDamageMultiplier() {
        return damageMultiplier;
    }
}
