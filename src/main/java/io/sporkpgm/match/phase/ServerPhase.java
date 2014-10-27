package io.sporkpgm.match.phase;

import io.sporkpgm.Spork;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.match.Match;
import io.sporkpgm.match.MatchPhase;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;

public abstract class ServerPhase implements Runnable {

	boolean complete;
	int duration = 0;
	Match match;
	MatchPhase phase;

	public Match getMatch() {
		return match;
	}

	public MatchPhase getPhase() {
		return phase;
	}

	public int getSeconds() {
		int fill = 20 - (duration % 20);
		fill = (fill == 20 ? 0 : fill);

		return (duration + fill) / 20;
	}

	public void setSeconds(int seconds) {
		this.duration = seconds * 20;
	}

	public int getTicks() {
		return duration;
	}

	public void setTicks(int ticks) {
		this.duration = ticks;
	}

	public boolean isFullSecond() {
		return getTicks() % 20 == 0;
	}

	public void broadcast(String message) {
		Spork.get().getServer().broadcastMessage(message);
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public String getMessage() {
		return null;
	}

	public FireworkEffect getFirework() {
		SporkMap map = match.getMap();
		Color color = getColor(map.getWinner().getColor());

		FireworkEffect.Builder builder = FireworkEffect.builder();
		builder.withColor(color, changeBrightness(-0x55, color));
		builder.with(Type.BALL_LARGE);
		builder.trail(false);
		builder.flicker(true);
		return builder.build();
	}

	/**
	 * Darkens the color using {@link java.awt.Color}'s darken method
	 *
	 * @return A new {@link org.bukkit.Color} darkened
	 */
	public static Color darker(Color color) {
		java.awt.Color color2 = new java.awt.Color(color.getRed(), color.getBlue(), color.getGreen());
		java.awt.Color darker = color2.darker();
		return Color.fromRGB(darker.getRed(), darker.getGreen(), darker.getBlue());
	}

	/**
	 * Brightens the color using {@link java.awt.Color}'s brighter method
	 *
	 * @return A new {@link org.bukkit.Color} brightened
	 */
	public static Color brighter(Color color) {
		java.awt.Color color2 = new java.awt.Color(color.getRed(), color.getBlue(), color.getGreen());
		java.awt.Color brighter = color2.brighter();
		return Color.fromRGB(brighter.getRed(), brighter.getGreen(), brighter.getBlue());
	}

	/**
	 * Changes the brightness by a given offset
	 *
	 * @param offset An offset, can be positive (brighter) or negative (darker)
	 * @return A new {@link Color} object with its brightness modified.
	 * For example, applying changeBrightness(-0x23) to a Color object with its
	 * hex value as #4719ff will return a new Color object with its hex value as
	 * #2400dc.
	 */
	public static Color changeBrightness(int offset, Color color) {
		int r = color.getRed() - offset;
		int g = color.getGreen() - offset;
		int b = color.getBlue() - offset;
		return Color.fromRGB(r <= 0 ? 0 : (r >= 255 ? 255 : r), g <= 0 ? 0 : (g >= 255 ? 255 : g), b <= 0 ? 0 : (b >= 255 ? 255 : b));
	}

	/**
	 * Gets a {@link org.bukkit.Color} object associated with this color
	 *
	 * @return Associative {@link org.bukkit.Color} with the given code
	 */
	public static Color getColor(ChatColor color) {
		switch(color) {
			case DARK_BLUE:
				return Color.fromRGB(0x0000aa);
			case DARK_GREEN:
				return Color.fromRGB(0x00aa00);
			case DARK_AQUA:
				return Color.fromRGB(0x00aaaa);
			case DARK_RED:
				return Color.fromRGB(0xaa0000);
			case DARK_PURPLE:
				return Color.fromRGB(0xaa00aa);
			case GOLD:
				return Color.fromRGB(0xffaa00);
			case GRAY:
				return Color.fromRGB(0xaaaaaa);
			case DARK_GRAY:
				return Color.fromRGB(0x555555);
			case BLUE:
				return Color.fromRGB(0x5555ff);
			case GREEN:
				return Color.fromRGB(0x55ff55);
			case AQUA:
				return Color.fromRGB(0x55ffff);
			case RED:
				return Color.fromRGB(0xff5555);
			case LIGHT_PURPLE:
				return Color.fromRGB(0xff55ff);
			case YELLOW:
				return Color.fromRGB(0xffff55);
			case WHITE:
				return Color.WHITE;
			default:
				return Color.BLACK;
		}
	}

}
