package io.sporkpgm.util;

import io.sporkpgm.Spork;
import org.bukkit.scheduler.BukkitTask;

@SuppressWarnings("ALL")
public class SchedulerUtil {

	private Runnable stopRunnable = new Runnable() {

		@Override
		public void run() {
			running = false;
		}

	};

	BukkitTask task;
	boolean running;
	Runnable run;
	boolean async;

	public SchedulerUtil(Runnable run, boolean async) {
		this.run = run;
		this.running = false;
		this.async = async;
	}

	public boolean isRunning() {
		return running || task != null;
	}

	public Runnable getRunnable() {
		return run;
	}

	public void delay(long delay) {
		if(async)
			this.task = Spork.get().getServer().getScheduler().runTaskLaterAsynchronously(Spork.get(), run, delay);
		else
			this.task = Spork.get().getServer().getScheduler().runTaskLater(Spork.get(), run, delay);
		this.running = true;

		Spork.get().getServer().getScheduler().scheduleSyncDelayedTask(Spork.get(), stopRunnable, delay);
	}

	public boolean startRepeat(long period) {
		return startRepeat(0, period);
	}

	public boolean startRepeat(long delay, long period) {
		if(running)
			return false;
		if(async)
			this.task = Spork.get().getServer().getScheduler().runTaskTimerAsynchronously(Spork.get(), run, delay, period);
		else
			this.task = Spork.get().getServer().getScheduler().runTaskTimer(Spork.get(), run, delay, period);
		this.running = true;
		return true;
	}

	public boolean stopRepeat() {
		if(!running)
			return false;
		Spork.get().getServer().getScheduler().cancelTask(this.task.getTaskId());
		this.running = false;
		this.task = null;
		return true;
	}

	public void run() {
		if(async)
			Spork.get().getServer().getScheduler().runTaskAsynchronously(Spork.get(), run);
		else
			Spork.get().getServer().getScheduler().runTask(Spork.get(), run);
	}

}
