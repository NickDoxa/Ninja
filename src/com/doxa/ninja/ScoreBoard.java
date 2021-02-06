package com.doxa.ninja;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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
				ChatColor.translateAlternateColorCodes('&', "&8&l| &6&lNinja &8&l|"));
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score = obj.getScore(ChatColor.DARK_GRAY + "-------------------");
		score.setScore(2);
		Score score2 = obj.getScore(ChatColor.AQUA + "Total Kills: " + ChatColor.DARK_AQUA + 
				(player.getStatistic(Statistic.PLAYER_KILLS) + player.getStatistic(Statistic.MOB_KILLS)));
		score2.setScore(1);
		Score score3 = obj.getScore(ChatColor.AQUA + "Player Kills: " + ChatColor.DARK_AQUA + 
										player.getStatistic(Statistic.PLAYER_KILLS));
		score3.setScore(0);
		player.setScoreboard(board);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		createBoard(player);
	}
	
	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player && event.getEntity().getKiller() instanceof Player) {
			Player player = (Player) event.getEntity().getKiller();
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			createBoard(player);
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
			Player player = (Player) event.getEntity().getKiller();
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			createBoard(player);
		}
	}
}
