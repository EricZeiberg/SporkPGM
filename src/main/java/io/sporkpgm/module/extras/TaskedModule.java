package io.sporkpgm.module.extras;

public interface TaskedModule {

	/*
	 * A TaskedModule is a Module extension to provide support for starting tasks when a Module is loaded.
	 * Examples include a looping task which runs every second for fetching information about something.
	 */

	public void setTasks(boolean tasks);

}
