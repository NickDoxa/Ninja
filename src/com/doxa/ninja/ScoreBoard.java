package com.doxa.ninja;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreBoard implements Listener {
	
	Main plugin;
	public ScoreBoard(Main main) {
		this.plugin = main;
	}
	
	public void createBoard(Player player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective obj = board.registerNewObjective("NinjaBoard", "dummy",
				ChatColor.translateAlternateColorCodes('&', "&8&l>&7&l> &e&lNinja &7&l<&8&l<"));
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score_under = obj.getScore(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "_____________");
		score_under.setScore(6);
		Score score_blank1 = obj.getScore(ChatColor.DARK_GRAY + "");
		score_blank1.setScore(5);
		Score score_total = obj.getScore(ChatColor.AQUA + "" + ChatColor.BOLD + "Total Kills: " + ChatColor.DARK_AQUA + 
				(player.getStatistic(Statistic.PLAYER_KILLS) + player.getStatistic(Statistic.MOB_KILLS)));
		score_total.setScore(4);
		Score score_player = obj.getScore(ChatColor.AQUA + "" + ChatColor.BOLD + "Player Kills: " + ChatColor.DARK_AQUA + 
										player.getStatistic(Statistic.PLAYER_KILLS));
		score_player.setScore(3);
		Score score_blank = obj.getScore(ChatColor.DARK_GRAY + " ");
		score_blank.setScore(2);
		Score score_level = obj.getScore(ChatColor.AQUA + "" + ChatColor.BOLD + "Level: " + ChatColor.DARK_AQUA + plugin.getNinjaLevel(player));
		score_level.setScore(1);
		Score score_blank2 = obj.getScore("");
		score_blank2.setScore(0);
		player.setScoreboard(board);
	}
	
	public void updateBoard(Player player) {
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		createBoard(player);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		createBoard(player);
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
			Player player = (Player) event.getEntity().getKiller();
			updateBoard(player);
		}
	}
}
