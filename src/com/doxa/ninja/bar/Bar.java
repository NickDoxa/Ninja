package com.doxa.ninja.bar;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import com.doxa.ninja.Main;
import com.doxa.ninja.Main.MoveType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Bar {
	
	Main plugin;
	public Bar(Main main) {
		plugin = main;
	}
	
	Map<Player, BossBar> bar_map = new HashMap<Player, BossBar>();
	Map<Player, Double> chakra_map = new HashMap<Player, Double>();

	public BossBar getBar(Player player) {
		return bar_map.get(player);
	}
	
	public double getChakra(Player player) {
		return chakra_map.get(player);
	}
	
	public void createBar(Player player) {
		BossBar bar = Bukkit.createBossBar(format("&b&L" + player.getName() + "'s &b&lChakra"), BarColor.BLUE, BarStyle.SOLID);
		bar.setVisible(true);
		bar.addPlayer(player);
		bar_map.put(player, bar);
		chakra_map.put(player, 1.0);
		cast(getChakra(player), player);
		
	}
	
	public void cast(double amt, Player player) {
		if (amt > 1.0) {
			getBar(player).setProgress(1.0);
		} else if (amt < 0) {
			getBar(player).setProgress(0);
		} else {
			getBar(player).setProgress(amt);
		}
	}
	
	private String format(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public void useChakra(double d, Player player) {
		double chakra_amt = chakra_map.get(player);
		double minus_amt = chakra_amt -= d;
		if (minus_amt <= 0) {
			cast(0, player);
			chakra_map.put(player, 0d);
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "" + ChatColor.BOLD + "Chakra Overloaded!"));
			player.damage(plugin.getDamage(MoveType.CHAKRA_OVERLOAD));
		} else {
			chakra_map.put(player, minus_amt);
			cast(minus_amt, player);
		}
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
            	double chakra_amt = chakra_map.get(player);
            	double add_amt = chakra_amt += d;
            	if (add_amt >= 1.0) {
            		chakra_map.put(player, 1.0);
            		cast(1.0, player);
            	} else {
            		chakra_map.put(player, add_amt);
            		cast(add_amt, player);
            	}
            }
		}, 30*20);
	}
}