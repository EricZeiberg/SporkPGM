package io.sporkpgm.objective.scored;

import com.google.common.collect.Lists;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.exceptions.ModuleLoadException;
import io.sporkpgm.objective.ObjectiveModule;
import io.sporkpgm.team.SporkTeam;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "ScoredObjective", description = "Objective based on score", multiple = false)
public class ScoredObjective extends ObjectiveModule {

	public static int NO_LIMIT = 0;

	int score = 0;
	int limit = NO_LIMIT;
	boolean complete;

	SporkTeam team;

	public ScoredObjective(SporkTeam team) throws ModuleLoadException {
		this(team, NO_LIMIT);
	}

	public ScoredObjective(SporkTeam team, int limit) throws ModuleLoadException {
		super(null, team);
		this.team = team;
		this.limit = limit;

		if(!hasLimit() && !hasTimer())
			throw new ModuleLoadException("ScoredObjectives require either a timer or a score limit");

		team.setScored(this);
	}

	public boolean hasLimit() {
		return limit > NO_LIMIT;
	}

	public boolean hasTimer() {
		return getMap().getTimer() != null;
	}

	public boolean checkComplete() {
		try {
			if(hasTimer()) {
				if(getMap().getTimer().isComplete()) {
					// Log.info("Timer is complete");
					return isHighest();
				}
			}
		} catch(Exception e) {
			return false;
		}

		if(hasLimit()) {
			// Log.info("Score: " + score + " >= " + limit + " = " + (score >= limit));
			if(score >= limit) {
				return isHighest();
			}
		}

		return false;
	}

	public boolean isHighest() {
		return getHighest(getMap()).contains(this);
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int... scores) {
		for(int score : scores) {
			this.score = score;
			getMap().getObjective().getScore(getPlayer()).setScore(score);
			if(checkComplete())
				setComplete(true);
		}
	}

	public void addScore(int... amounts) {
		for(int amount : amounts) {
			setScore(getScore() + amount);
		}
	}

	public void takeScore(int... amounts) {
		for(int amount : amounts) {
			addScore(-amount);
		}
	}

	public String getPlayer() {
		return getTeam().getPlayer();
	}

	public SporkTeam getTeam() {
		return team;
	}

	private SporkMap getMap() {
		return team.getMap();
	}

	public void update() {
		// do nothing
	}

	public ChatColor getStatusColour() {
		return null;
	}

	public Class<? extends Builder> builder() {
		return ScoredBuilder.class;
	}

	public static List<ScoredObjective> getHighest(SporkMap map) {
		List<Module> modules = map.getModules(ScoredObjective.class);
		List<ScoredObjective> objectives = new ArrayList<>();
		for(Module module : modules) {
			objectives.add((ScoredObjective) module);
		}

		List<ScoredObjective> scores = Lists.newArrayList(objectives.get(0));
		int highest = scores.get(0).getScore();

		for(ScoredObjective score : objectives) {
			if(!scores.contains(score)) {
				if(score.getScore() > highest) {
					highest = score.getScore();
					scores.clear();
					scores.add(score);
				} else if(score.getScore() == highest) {
					scores.add(score);
				}
			}
		}

		return scores;
	}

}
