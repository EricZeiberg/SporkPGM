package io.sporkpgm.filter.other;

public enum State {

	ALLOW, // Filter is allowed
	DENY, // Filter is denied
	ABSTAIN; // Filter has no opinion

	public boolean allowed() {
		return toBoolean();
	}

	public boolean denied() {
		return !toBoolean();
	}

	public boolean toBoolean() {
		return this == ALLOW || this == ABSTAIN;
	}

	public State reverse() {
		if(this == ALLOW) {
			return DENY;
		} else if(this == DENY) {
			return ALLOW;
		}

		return ABSTAIN;
	}

	public static State fromBoolean(Boolean b) {
		return b ? ALLOW : DENY;
	}

}
