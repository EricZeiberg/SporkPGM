package io.sporkpgm.player.rank;

public class Permission {

	String permission;
	int priority;
	boolean equalMode;

	public Permission(String permission) {
		this(permission, 0, false);
	}

	public Permission(String permission, int priority) {
		this(permission, priority, false);
	}

	public Permission(String permission, boolean equalMode) {
		this(permission, 0, equalMode);
	}

	public Permission(String permission, int priority, boolean equalMode) {
		this.permission = permission;
		this.priority = priority;
		this.equalMode = equalMode;
	}

	public String getPermission() {
		return permission;
	}

	public int getPriority() {
		return priority;
	}

	public boolean isEqualMode() {
		return equalMode;
	}

	public boolean applied(Rank rank) {
		return (isEqualMode() && rank.getPriority() == getPriority()) || (!isEqualMode() && rank.getPriority() >= getPriority());
	}

	@Override
	public String toString() {
		return "Permission{permission=" + permission + ",priority=" + priority + "}";
	}

}
