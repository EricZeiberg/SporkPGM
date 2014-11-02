package io.sporkpgm.module.modules.tnt;

/**
 * Created by Eric Zeiberg on 11/2/14.
 * Copyrighted unless stated otherwise
 */
public class TNTSettings {

    boolean instantIgnite = false;
    boolean blockDamage = true;
    double yield = 1.0;
    double power = 20.0;
    String fuse = null;

    public TNTSettings(boolean instantIgnite, boolean blockDamage, double yield, double power, String fuse) {
        this.instantIgnite = instantIgnite;
        this.blockDamage = blockDamage;
        this.yield = yield;
        this.power = power;
        this.fuse = fuse;
    }

    public boolean isInstantIgnite() {
        return instantIgnite;
    }

    public boolean isBlockDamage() {
        return blockDamage;
    }

    public double getYield() {
        return yield;
    }

    public double getPower() {
        return power;
    }

    public String getFuse() {
        return fuse;
    }
}
