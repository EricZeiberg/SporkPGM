package io.sporkpgm.module.modules.info;

public class Contributor {

	String username;
	String contribution;

	public Contributor(String username, String contribution) {
		this.username = username;
		this.contribution = contribution;
	}

	public boolean hasContribution() {
		return contribution != null;
	}

	public String getUsername() {
		return username;
	}

	public String getContribution() {
		return contribution;
	}

	@Override
	public String toString() {
		return getUsername();
	}

}
