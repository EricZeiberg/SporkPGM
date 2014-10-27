package io.sporkpgm.filter.other;

import io.sporkpgm.filter.exceptions.InvalidContextException;
import io.sporkpgm.map.event.BlockChangeEvent;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.player.event.PlayingPlayerMoveEvent;
import org.bukkit.Location;

public class Context {

	boolean denied;
	boolean messaged;

	SporkPlayer player;
	BlockChangeEvent block;
	BlockChangeEvent transformation;
	PlayingPlayerMoveEvent movement;

	public Context(SporkPlayer player, BlockChangeEvent block, BlockChangeEvent transformation, PlayingPlayerMoveEvent movement) {
		this.player = player;
		this.block = block;
		this.transformation = transformation;
		this.movement = movement;
	}
	
	public Context(Object... objects) throws InvalidContextException {
		for(Object object : objects) {
			fill(object);
		}
	}

	private void fill(Object object) throws InvalidContextException {
		if(object instanceof SporkPlayer) {
			this.player = (SporkPlayer) object;
		} else if(object instanceof BlockChangeEvent) {
			BlockChangeEvent event = (BlockChangeEvent) object;
			if(event.hasPlayer()) {
				this.block = event;
				this.player = event.getPlayer();
			} else {
				this.transformation = event;
			}
		} else if(object instanceof PlayingPlayerMoveEvent) {
			PlayingPlayerMoveEvent event = (PlayingPlayerMoveEvent) object;
			this.movement = event;
			this.player = event.getPlayer();
		}

		/*
		throw new InvalidContextException("Attempted to supply an Object which was unsupported");
		*/
	}

	public boolean isDenied() {
		return denied;
	}

	public boolean isMessaged() {
		return messaged;
	}

	public void setMessaged(boolean messaged) {
		this.messaged = messaged;
	}

	public SporkPlayer getPlayer() {
		return player;
	}

	public boolean hasPlayer() {
		return player != null;
	}

	public BlockChangeEvent getBlock() {
		return block;
	}

	public boolean hasBlock() {
		return block != null;
	}

	public BlockChangeEvent getTransformation() {
		return transformation;
	}

	public boolean hasTransformation() {
		return transformation != null;
	}

	public BlockChangeEvent getModification() {
		return block != null ? block : transformation;
	}

	public boolean hasModification() {
		return getModification() != null;
	}

	public PlayingPlayerMoveEvent getMovement() {
		return movement;
	}

	public boolean hasMovement() {
		return movement != null;
	}

	public void deny() {
		this.denied = true;

		if(hasModification()) {
			getModification().setCancelled(true);
		}

		if(hasMovement()) {
			PlayingPlayerMoveEvent move = getMovement();
			double x = Math.floor(move.getFrom().getX()) + 0.5D;
			double y = move.getFrom().getY();
			double z = Math.floor(move.getFrom().getZ()) + 0.5D;

			float yaw = move.getTo().getYaw();
			float pitch = move.getTo().getPitch();
			getPlayer().getPlayer().teleport(new Location(move.getFrom().getWorld(), x, y, z, yaw, pitch));
		}
	}

}
