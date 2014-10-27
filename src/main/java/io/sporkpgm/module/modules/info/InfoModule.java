package io.sporkpgm.module.modules.info;

import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.util.StringUtil;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "InfoModule", description = "Contains information about the map", listener = false, multiple = false)
public class InfoModule extends Module {

	private String name;
	private String version;
	private String objective;
	private List<String> rules;
	private int maxPlayers;

	private List<Contributor> authors;
	private List<Contributor> contributors;

	public InfoModule(String name, String version, String objective, List<String> rules, int maxPlayers) {
		this(name, version, objective, rules, maxPlayers, new ArrayList<Contributor>(), new ArrayList<Contributor>());
	}

	public InfoModule(String name, String version, String objective, List<String> rules, int maxPlayers, List<Contributor> authors, List<Contributor> contributors) {
		this.name = name;
		this.version = version;
		this.objective = objective;
		this.rules = rules;
		this.maxPlayers = maxPlayers;
		this.authors = authors;
		this.contributors = contributors;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getObjective() {
		return objective;
	}

	public List<String> getRules() {
		return rules;
	}

	public List<Contributor> getAuthors() {
		return authors;
	}

	public List<Contributor> getContributors() {
		return contributors;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public String getShortDescription() {
		String authorsString = StringUtil.listToEnglishCompound(authors, ChatColor.RED + "", ChatColor.DARK_PURPLE + "");
		return ChatColor.GOLD + name + ChatColor.DARK_PURPLE + " by " + authorsString;
	}

	public Class<? extends Builder> builder() {
		return InfoBuilder.class;
	}

}
