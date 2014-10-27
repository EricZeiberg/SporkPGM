package io.sporkpgm.objective.monument;

import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.region.types.BlockRegion;

public class MonumentBlock {

	boolean broken;
	SporkPlayer player;
	BlockRegion block;

	public MonumentBlock(BlockRegion block) {
		this.block = block;
	}

	public void setComplete(SporkPlayer player, boolean broken) {
		setPlayer(player);
		setBroken(broken);
	}

	public void setBroken(boolean broken) {
		this.broken = broken;
	}

	public void setPlayer(SporkPlayer player) {
		this.player = player;
	}

	public boolean isBroken() {
		return broken;
	}

	public boolean isComplete() {
		return broken;
	}

	public SporkPlayer getPlayer() {
		return player;
	}

	public BlockRegion getBlock() {
		return block;
	}

}
