package io.sporkpgm.objective;

public enum GameCategory {

	FFA("Free for All"),
	ETO("Explode the Orb"),
	RECHARGE("Recharge"),
	TDM("Team Deathmatch"),
	SCORE("Score"),
	CTB("Capture the Block"),
	UNKNOWN("Unknown");

	private String fullName;

	GameCategory(String fullName) {
		this.fullName = fullName;
	}

}
