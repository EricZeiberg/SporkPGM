package io.sporkpgm.module.extras;

public interface InitModule {

	/*
	 * An InitModule is an extension onto Modules to provide start/stop methods for when a Module is loaded/unloaded.
	 * Examples are loading a list of Locations when the Module is loaded to be used during the match.
	 */

	public void start();

	public void stop();

}
