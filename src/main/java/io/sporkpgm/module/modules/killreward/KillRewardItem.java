package io.sporkpgm.module.modules.killreward;

import org.bukkit.Material;

/**
 * Created by Eric Zeiberg on 11/2/14.
 * Copyrighted unless stated otherwise
 */
public class KillRewardItem {

    Material material;
    int amount;

    public KillRewardItem(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }
}
